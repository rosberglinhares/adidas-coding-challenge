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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Api(tags = "Consents")
@RestController
@RequestMapping(path = "/consents")
public class ConsentController {
    @Autowired
    private ConsentService consentService;
    
    @Autowired
    private ConsentWordingService consentWordingService;
    
    @ApiOperation(
        value = "Adds a consent wording in the system.",
        notes = "Required roles: **ADMIN** or **LEGAL_DEPARTMENT**",
        authorizations = @Authorization("token-based")
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Consent wording added successfully"),
        @ApiResponse(code = 400, message = "The version label already exists")
    })
    @PostMapping("/wordings")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LEGAL_DEPARTMENT')")
    public ConsentWording addConsentWording(@ApiParam("The consent wording to be added in the system. The version label and the wording must be informed.")
            @RequestBody ConsentWording consentWording) {
        return this.consentWordingService.addConsentWordingVersion(consentWording);
    }
    
    @ApiOperation(
        value = "Updates a consent wording.",
        notes = "Required roles: **ADMIN** or **LEGAL_DEPARTMENT**",
        authorizations = @Authorization("token-based")
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Consent wording updated successfully"),
        @ApiResponse(
            code = 400,
            message = "1. The consent wording version does not exists<br>" +
                      "2. The version label already exists<br>" +
                      "3. The consent wording is already attached to some consumer")
    })
    @PutMapping("/wordings/{version}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LEGAL_DEPARTMENT')")
    public ConsentWording updateConsentWording(@PathVariable("version") int consentWordingVersion,
            @ApiParam("The consent wording to be updated. The version label and the wording are required.")
            @RequestBody ConsentWording consentWording) {
        consentWording.setVersion(consentWordingVersion);
        return this.consentWordingService.updateConsentWording(consentWording);
    }
    
    @ApiOperation(
        value = "Gets a consent wording.",
        notes = "Required roles: **ADMIN** or **LEGAL_DEPARTMENT**",
        authorizations = @Authorization("token-based")
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Consent wording found successfully"),
        @ApiResponse(code = 400, message = "The consent wording version does not exists")
    })
    @GetMapping("/wordings/{version}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LEGAL_DEPARTMENT')")
    public ConsentWording getConsentWording(@PathVariable("version") int consentWordingVersion) {
        return this.consentWordingService.getByVersion(consentWordingVersion);
    }
    
    @ApiOperation(
        value = "Deletes a consent wording.",
        notes = "Required roles: **ADMIN** or **LEGAL_DEPARTMENT**",
        authorizations = @Authorization("token-based")
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Consent wording deleted successfully"),
        @ApiResponse(
            code = 400,
            message = "1. The consent wording version does not exists<br>" +
                      "2. The consent wording is already attached to some consumer")
    })
    @DeleteMapping("/wordings/{version}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LEGAL_DEPARTMENT')")
    public void deleteConsentWording(@PathVariable("version") int consentWordingVersion) {
        this.consentWordingService.deleteByVersion(consentWordingVersion);
    }
    
    @ApiOperation(
        value = "Gets the most recent consent wording. This is the wording to be shown to the consumer " +
                "when a consent is required.",
        notes = "Required roles: **CONSUMER**",
        authorizations = @Authorization("token-based")
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Consent wording found successfully"),
        @ApiResponse(code = 400, message = "No consent wordings registered yet")
    })
    @GetMapping("/wordings/current")
    @PreAuthorize("hasRole('CONSUMER')")
    public ConsentWording getCurrentConsentWording() {
        return this.consentWordingService.getCurrentConsentWording();
    }
    
    @ApiOperation(
        value = "Returns a value indicating if the user must give your consent, according to GDPR rules.",
        notes = "According to GDPR, the regulation applies in these two cases:<br>" +
                "<br>" +
                "1. Controllers and Processors established in the EU, regardless of whether the processing of personal" +
                "   data takes place in the EU;<br>" +
                "2. Controllers and Processors that target data subjects in the Union even if they are not established" +
                "   in the EU.<br>" +
                "<br>" +
                "Required roles: **CONSUMER**",
        authorizations = @Authorization("token-based")
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Operation completed successfully"),
        @ApiResponse(code = 400, message = "Some error occurred trying to get the IP localization")
    })
    @GetMapping("/is-required")
    @PreAuthorize("hasRole('CONSUMER')")
    public boolean isConsentRequired(@ApiParam(value = "The source IP", required = true) @RequestHeader("X-Forwarded-For") String sourceIp) {
        // The AWS Global Accelerator will preserve the client IP address in the X-Forwarded-For header.
        // See https://aws.amazon.com/blogs/aws/new-client-ip-address-preservation-for-aws-global-accelerator/ for details.
        return this.consentService.isConsentRequired(sourceIp);
    }
    
    @ApiOperation(
        value = "Allows the logged consumer to give consent to a wording.",
        notes = "According to GDPR:<br>" +
                "<br>" +
                "GDPR Art. 6 - Lawfulness of processing<br>" +
                "<br>" +
                "&nbsp;&nbsp;1. Processing shall be lawful only if and to the extent that at least one of the following applies:<br>" +
                "<br>" +
                "&nbsp;&nbsp;&nbsp;&nbsp;(a) the data subject has given consent to the processing of his or her personal data for one or more specific purposes;<br>" +
                "<br>" +
                "Required roles: **CONSUMER**",
        authorizations = @Authorization("token-based")
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Consent given successfully"),
        @ApiResponse(code = 400, message = "The consent wording version does not exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public Consent giveConsent(int consentWordingVersion) {
        return this.consentService.giveConsent(consentWordingVersion);
    }
    
    @ApiOperation(
        value = "Allows the logged consumer to withdraw consent to a wording.",
        notes = "After the withdrawal, according to GDPR, the personal data of this consumer must be erased:<br>" +
                "<br>" +
                "GDPR Art. 7 - Conditions for consent<br>" +
                "<br>" + 
                "&nbsp;&nbsp;3. The data subject shall have the right to withdraw his or her consent at any time<br>" +
                "<br>" + 
                "GDPR Art. 17 - Right to erasure (‘right to be forgotten’)<br>" +
                "<br>" + 
                "&nbsp;&nbsp;1. [...]<br>" +
                "<br>" +   
                "&nbsp;&nbsp;&nbsp;&nbsp;(b) the data subject withdraws consent on which the processing is based according to point (a) of" +
                "        Article 6(1), or point (a) of Article 9(2)<br>" +
                "<br>" +
                "Required roles: **CONSUMER**",
        authorizations = @Authorization("token-based")
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Consent withdrawn successfully"),
        @ApiResponse(code = 400, message = "The consent wording version does not exists")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public Consent withdrawConsent(int consentWordingVersion) {
        return this.consentService.withdrawConsent(consentWordingVersion);
    }
    
    @ApiOperation(
        value = "Gets consent information about a specific user.",
        notes = "If there is no consent information for the user, an empty result is returned.<br>" +
                "<br>" +
                "Required roles: **ADMIN**",
        authorizations = @Authorization("token-based")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Consent getConsentInfo(String userName) {
        return this.consentService.getLastConsentActionByUserName(userName);
    }
}