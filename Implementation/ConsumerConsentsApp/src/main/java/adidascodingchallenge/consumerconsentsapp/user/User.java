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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(
    description = "Represents a user in the system. A user can be an Administrator, a person from " +
                  "the Legal Department or a Consumer."
)
@Entity
@Table(name = "appuser",  // Avoid conflicts with the 'user' reserved word in PostgreSQL
    indexes = {
        @Index(name = "appuser_user_name_idx", columnList = "userName"),  // UserRepository.findByUserName
        @Index(name = "appuser_token_idx", columnList = "token")          // UserRepository.findByToken
    }
)
public class User implements Serializable {
    
    // Necessary to use the ConsentRepository.findLastConsentActionByUserName query
    private static final long serialVersionUID = -3279142854184241836L;

    @Id
    @GeneratedValue
    private int id;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Role role;
    
    @ApiModelProperty(required = true)
    @Column(nullable = false, unique = true, updatable = false)
    private String userName;
    
    @ApiModelProperty(required = true)
    @Transient
    private String password;
    
    @ApiModelProperty(hidden = true)
    @Column(nullable = false)
    private String passwordHash;
    
    @ApiModelProperty(value = "A token that allows the user authenticate in the system")
    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}