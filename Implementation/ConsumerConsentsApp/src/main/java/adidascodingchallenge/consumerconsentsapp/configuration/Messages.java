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

package adidascodingchallenge.consumerconsentsapp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class Messages {
    @Autowired
    private MessageSource messageSource;
    
    private final MessagesUser user = new MessagesUser();
    private final MessagesConsent consent = new MessagesConsent();
    private final MessagesConsentWording consentWording = new MessagesConsentWording();
    private final MessagesConsumer consumer = new MessagesConsumer();
    
    public class MessagesUser {
        public String userNamePasswordRequired() {
            return messageSource.getMessage("user.user_name_password_required", null, null);
        }
        
        public String invalidUserNameOrPassword() {
            return messageSource.getMessage("user.invalid_user_name_or_password", null, null);
        }
    }
    
    public class MessagesConsent {
        public String errorGettingLocationForIp(String ip, String innerErrorMessage) {
            return messageSource.getMessage("consent.error_getting_location_for_ip",
                    new Object[] {ip, innerErrorMessage}, null);
        }
        
        public String sourceIpRequired() {
            return messageSource.getMessage("consent.source_ip_required", null, null);
        }
    }
    
    public class MessagesConsentWording {
        public String cannotUpdateOrDeleteAttached() {
            return messageSource.getMessage("consent_wording.cannot_update_or_delete_attached", null, null);
        }
        
        public String notFound(int consentWordingVersion) {
            return messageSource.getMessage("consent_wording.not_found", new Object[] { consentWordingVersion }, null);
        }
        
        public String noConsentWordingRegisteredYet() {
            return messageSource.getMessage("consent_wording.no_consent_wording_registered_yet", null, null);
        }
        
        public String versionLabelRequired() {
            return messageSource.getMessage("consent_wording.version_label_required", null, null);
        }
        
        public String wordingRequired() {
            return messageSource.getMessage("consent_wording.wording_required", null, null);
        }
        
        public String versionLabelAlreadyExists(String versionLabel) {
            return messageSource.getMessage("consent_wording.version_label_already_exists",
                    new Object[] { versionLabel }, null);
        }
    }
    
    public class MessagesConsumer {
        public String userNameAlreadyExists(String userName) {
            return messageSource.getMessage("consumer.user_name_already_exists", new Object[] { userName }, null);
        }
        
        public String notFound(int consumerId) {
            return messageSource.getMessage("consumer.not_found", new Object[] { consumerId }, null);
        }
        
        public String operationNotAllowed(int consumerId) {
            return messageSource.getMessage("consumer.operation_not_allowed", new Object[] { consumerId }, null);
        }
    }

    public MessagesUser user() {
        return this.user;
    }

    public MessagesConsent consent() {
        return this.consent;
    }
    
    public MessagesConsentWording consentWording() {
        return this.consentWording;
    }

    public MessagesConsumer consumer() {
        return this.consumer;
    }
}