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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Api(tags = "Consumers")
@RestController
@RequestMapping(path = "/consumers")
public class ConsumerController {
    @Autowired
    private ConsumerService consumerService;
    
    @ApiOperation(value = "Sign up a consumer in the system.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Consumer added successfully"),
        @ApiResponse(code = 400, message = "The user name already exists")
    })
    @PostMapping
    public Consumer signUp(@ApiParam("The consumer to be added in the system. At least the user name and password must be informed.")
            @RequestBody Consumer consumer) {
        return this.consumerService.signUp(consumer);
    }
    
    @ApiOperation(
        value = "Allows a consumer to update your personal data.",
        notes = "This operation satisfies the following GDPR rule:<br>" +
                "<br>" +
                "GDPR Art. 16: Right to rectification<br>" +
                "<br>" +
                "Required roles: **CONSUMER**",
        authorizations = @Authorization("token-based")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CONSUMER')")
    public Consumer update(@PathVariable("id") int consumerId, @RequestBody Consumer consumer) {
        consumer.setId(consumerId);
        return this.consumerService.update(consumer);
    }
    
    @ApiOperation(
        value = "Allows a consumer to get your personal data.",
        notes = "This operation satisfies the following GDPR rule:<br>" +
                "<br>" +
                "GDPR Art. 15: Right of access by the data subject<br>" +
                "<br>" +
                "Required roles: **CONSUMER**",
        authorizations = @Authorization("token-based")
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CONSUMER')")
    public Consumer get(@PathVariable("id") int consumerId) {
        return this.consumerService.getById(consumerId);
    }
    
    @ApiOperation(
        value = "Allows a consumer to delete your personal data.",
        notes = "This operation satisfies the following GDPR rule:<br>" +
                "<br>" +
                "GDPR Art. 17: Right to erasure (‘right to be forgotten’)<br>" +
                "<br>" +
                "Required roles: **CONSUMER**",
        authorizations = @Authorization("token-based")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CONSUMER')")
    public void delete(@PathVariable("id") int consumerId) {
        this.consumerService.deleteById(consumerId);
    }
}