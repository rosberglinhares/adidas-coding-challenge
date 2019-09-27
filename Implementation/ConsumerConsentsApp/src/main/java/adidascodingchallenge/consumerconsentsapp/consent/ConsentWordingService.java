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

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adidascodingchallenge.consumerconsentsapp.configuration.Messages;
import adidascodingchallenge.consumerconsentsapp.exception.BusinessException;

@Service
public class ConsentWordingService {
    @Autowired
    private ConsentWordingRepository consentWordingRepository;
    
    @Autowired
    private ConsentRepository consentRepository;
    
    @Autowired
    private Messages messages;
    
    public ConsentWording addConsentWordingVersion(ConsentWording consentWording) {
        this.perfomCommonConsentWordingValidations(consentWording);
        
        consentWording.setCreationDate(LocalDateTime.now());
        
        return this.consentWordingRepository.save(consentWording);
    }
    
    public ConsentWording updateConsentWording(ConsentWording consentWording) {
        if (!this.consentWordingRepository.existsById(consentWording.getVersion())) {
            throw new BusinessException(this.messages.consentWording().notFound(consentWording.getVersion()));
        }
        
        this.perfomCommonConsentWordingValidations(consentWording);
        
        if (this.consentRepository.findFirstByConsentWordingVersion(consentWording.getVersion()).isPresent()) {
            throw new BusinessException(this.messages.consentWording().cannotUpdateOrDeleteAttached());
        }
        
        return this.consentWordingRepository.save(consentWording);
    }
    
    public ConsentWording getByVersion(int consentWordingVersion) {
        ConsentWording consentWording = this.consentWordingRepository.findById(consentWordingVersion)
                .orElseThrow(() -> new BusinessException(this.messages.consentWording().notFound(consentWordingVersion)));
        
        return consentWording;
    }
    
    public void deleteByVersion(int consentWordingVersion) {
        if (!this.consentWordingRepository.existsById(consentWordingVersion)) {
            throw new BusinessException(this.messages.consentWording().notFound(consentWordingVersion));
        }
        
        if (this.consentRepository.findFirstByConsentWordingVersion(consentWordingVersion).isPresent()) {
            throw new BusinessException(this.messages.consentWording().cannotUpdateOrDeleteAttached());
        }
        
        this.consentWordingRepository.deleteById(consentWordingVersion);
    }
    
    public ConsentWording getCurrentConsentWording() {
        ConsentWording consentWording = this.consentWordingRepository.findTopByOrderByCreationDateDesc()
                .orElseThrow(() -> new BusinessException(this.messages.consentWording().noConsentWordingRegisteredYet()));
        
        return consentWording;
    }

    private void perfomCommonConsentWordingValidations(ConsentWording consentWording) {
        if (consentWording.getVersionLabel() == null || consentWording.getVersionLabel().isBlank()) {
            throw new BusinessException(this.messages.consentWording().versionLabelRequired());
        }
        
        if (consentWording.getWording() == null || consentWording.getWording().isBlank()) {
            throw new BusinessException(this.messages.consentWording().wordingRequired());
        }
        
        // Cannot have duplicate version labels
        if (this.consentWordingRepository.findByVersionLabelAndVersionNot(consentWording.getVersionLabel(), consentWording.getVersion()).isPresent()) {
            throw new BusinessException(
                    this.messages.consentWording().versionLabelAlreadyExists(consentWording.getVersionLabel()));
        }
    }
}