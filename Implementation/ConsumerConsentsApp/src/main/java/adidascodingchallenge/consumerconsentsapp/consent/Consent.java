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

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import adidascodingchallenge.consumerconsentsapp.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@Entity
@Table(
    indexes = {
        @Index(name = "consent_action_date_idx", columnList = "actionDate DESC")  // ConsentRepository.findLastConsentActionByUserName
    }
)
public class Consent {
    
    @Id
    @GeneratedValue
    private int id;
    
    // Once the consumer has withdrawn your consent wording, his/her personal data must be deleted from the database,
    // but we still want to know which user and when he/she took this action.
    @ManyToOne(optional = false)
    @JoinColumn(
        referencedColumnName = "userName",
        updatable = false,
        // Allows a user name to exist in the Consent table even if it is not in the User table
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)
    )
    private User user;
 
    @ManyToOne(optional = false)
    @JoinColumn(updatable = false)
    private ConsentWording consentWording;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime actionDate;
    
    @ApiModelProperty(value = "If consent or withdraw")
    @Column(nullable = false, updatable = false)
    private boolean consentGiven;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ConsentWording getConsentWording() {
        return consentWording;
    }

    public void setConsentWording(ConsentWording consentWording) {
        this.consentWording = consentWording;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }

    public boolean isConsentGiven() {
        return consentGiven;
    }

    public void setConsentGiven(boolean consentGiven) {
        this.consentGiven = consentGiven;
    }
}