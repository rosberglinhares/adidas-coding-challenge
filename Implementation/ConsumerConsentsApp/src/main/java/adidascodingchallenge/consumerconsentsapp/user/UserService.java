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

package adidascodingchallenge.consumerconsentsapp.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import adidascodingchallenge.consumerconsentsapp.configuration.Messages;
import adidascodingchallenge.consumerconsentsapp.configuration.properties.SpringProperties;
import adidascodingchallenge.consumerconsentsapp.exception.BusinessException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private SpringProperties springProperties;
    
    @Autowired
    private Messages messages;
    
    public void insertDefaultAdminUser() {
        if (!this.userNameExists(this.springProperties.getSecurity().getUser().getName())) {
            User adminUser = new User();
            adminUser.setRole(Role.ADMIN);
            adminUser.setUserName(this.springProperties.getSecurity().getUser().getName());
            adminUser.setPasswordHash(this.passwordEncoder.encode(
                    this.springProperties.getSecurity().getUser().getPassword()));
            
            this.userRepository.save(adminUser);
        }
    }
    
    public boolean userNameExists(String userName) {
        return this.userRepository.findByUserName(userName).isPresent();
    }
    
    public String login(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException(this.messages.user().userNamePasswordRequired());
        }
        
        Optional<User> optionalDatabaseUser = this.userRepository.findByUserName(userName);
        
        if (optionalDatabaseUser.isPresent()) {
            User databaseUser = optionalDatabaseUser.get();
            
            if (this.passwordEncoder.matches(password, databaseUser.getPasswordHash())) {
                String token = UUID.randomUUID().toString();
                
                databaseUser.setToken(token);
                this.userRepository.save(databaseUser);
                
                return token;
            }
        }
        
        throw new BusinessException(this.messages.user().invalidUserNameOrPassword());
    }
    
    public Optional<User> findByToken(String token) {
        return this.userRepository.findByToken(token);
    }
    
    public User getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User)auth.getPrincipal();
    }
    
    @Bean
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}