/*
 * MIT License
 * 
 * Copyright (c) 2019 Rosberg Linhares (rosberglinhares@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package adidascodingchallenge.consumerconsentsapp.consent;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;

import adidascodingchallenge.consumerconsentsapp.configuration.Messages;
import adidascodingchallenge.consumerconsentsapp.configuration.Resources;
import adidascodingchallenge.consumerconsentsapp.configuration.properties.ApplicationProperties;
import adidascodingchallenge.consumerconsentsapp.consumer.ConsumerService;
import adidascodingchallenge.consumerconsentsapp.exception.BusinessException;
import adidascodingchallenge.consumerconsentsapp.user.User;
import adidascodingchallenge.consumerconsentsapp.user.UserService;

@Service
public class ConsentService {
    @Autowired
    private ConsentRepository consentRepository;
    
    @Autowired
    private ConsentWordingService consentWordingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private Resources resources;
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    private Messages messages;
    
    public boolean isConsentRequired(String sourceIp) {
        if (sourceIp == null || sourceIp.isBlank()) {
            throw new BusinessException(this.messages.consent().sourceIpRequired());
        }
        
        // According to GDPR, the regulation applies in these two cases:
        //
        // 1. Controllers and Processors established in the EU, regardless of whether the processing of personal
        //    data takes place in the EU
        //
        // 2. Controllers and Processors that target data subjects in the Union even if they are not established
        //    in the EU
        return this.applicationProperties.isGdprControllerEstablishedInTheEu() || this.isIpFromEurope(sourceIp);
    }
    
    private boolean isIpFromEurope(String sourceIp) {
        try (InputStream countryDatabaseInputStream = this.resources.getGeoLite2CountryDatabase().getInputStream()) {
            DatabaseReader geoIp2DbReader = new DatabaseReader.Builder(countryDatabaseInputStream).build();
            
            InetAddress sourceIpInetAddress = InetAddress.getByName(sourceIp);
            CountryResponse countryResponse = geoIp2DbReader.country(sourceIpInetAddress);
            
            boolean isInEurope = countryResponse.getContinent().getCode().equals(
                    this.applicationProperties.getConstants().getEuContinentCode());
            
            return isInEurope;
        } catch (IOException | GeoIp2Exception ex) {
            throw new BusinessException(this.messages.consent().errorGettingLocationForIp(sourceIp, ex.getMessage()));
        }
    }
    
    public Consent giveConsent(int consentWordingVersion) {
        User loggedUser = this.userService.getLoggedUser();
        
        Consent consent = new Consent();
        consent.setUser(loggedUser);
        consent.setConsentWording(this.consentWordingService.getByVersion(consentWordingVersion));
        consent.setActionDate(LocalDateTime.now());
        consent.setConsentGiven(true);
        
        return this.consentRepository.save(consent);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public Consent withdrawConsent(int consentWordingVersion) {
        User loggedUser = this.userService.getLoggedUser();
        
        Consent consent = new Consent();
        consent.setUser(loggedUser);
        consent.setConsentWording(this.consentWordingService.getByVersion(consentWordingVersion));
        consent.setActionDate(LocalDateTime.now());
        consent.setConsentGiven(false);
        
        consent = this.consentRepository.save(consent);
        
        // According to GDPR Art. 17, item 1 (b), we must erasure personal data if the consumer withdraw your consent
        this.consumerService.deleteByUserId(loggedUser.getId());
        
        return consent;
    }
    
    public Consent getLastConsentActionByUserName(String userName) {
        return this.consentRepository.findLastConsentActionByUserName(userName);
    }
}