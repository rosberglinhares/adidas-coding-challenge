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

package adidascodingchallenge.consumerconsentsapp.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import adidascodingchallenge.consumerconsentsapp.configuration.Messages;
import adidascodingchallenge.consumerconsentsapp.exception.BusinessException;
import adidascodingchallenge.consumerconsentsapp.user.Role;
import adidascodingchallenge.consumerconsentsapp.user.User;
import adidascodingchallenge.consumerconsentsapp.user.UserService;

@Service
public class ConsumerService {
    @Autowired
    private ConsumerRepository consumerRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private Messages messages;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Consumer signUp(Consumer consumer) {
        if (consumer.getUser() == null
                || consumer.getUser().getUserName() == null || consumer.getUser().getUserName().isBlank() 
                || consumer.getUser().getPassword() == null || consumer.getUser().getPassword().isBlank()) {
            throw new BusinessException(this.messages.user().userNamePasswordRequired());
        }
        
        if (this.userService.userNameExists(consumer.getUser().getUserName())) {
            throw new BusinessException(this.messages.consumer().userNameAlreadyExists(consumer.getUser().getUserName()));
        }
        
        consumer.getUser().setRole(Role.CONSUMER);
        consumer.getUser().setPasswordHash(this.passwordEncoder.encode(consumer.getUser().getPassword()));
        
        return this.consumerRepository.save(consumer);
    }
    
    public Consumer update(Consumer consumer) {
        // Only the logged user can update your data
        this.checkIfLoggedConsumerMatches(consumer.getId());
        
        return this.consumerRepository.save(consumer);
    }
    
    public Consumer getById(int consumerId) {
        // Only the logged user can get your data
        this.checkIfLoggedConsumerMatches(consumerId);
        
        return this.consumerRepository.findById(consumerId).get();
    }
    
    public void deleteById(int consumerId) {
        // Only the logged user can delete yourself
        this.checkIfLoggedConsumerMatches(consumerId);
        
        this.consumerRepository.deleteById(consumerId);
    }
    
    public void deleteByUserId(int userId) {
        Consumer consumer = this.consumerRepository.findByUserId(userId).get();
        this.consumerRepository.deleteById(consumer.getId());
    }
    
    private void checkIfLoggedConsumerMatches(int consumerId) {
        User loggedUser = this.userService.getLoggedUser();
        Consumer loggedConsumer = this.consumerRepository.findByUserId(loggedUser.getId()).get();
        
        if (consumerId != loggedConsumer.getId()) {
            throw new BusinessException(this.messages.consumer().operationNotAllowed(loggedConsumer.getId()));
        }
    }
}