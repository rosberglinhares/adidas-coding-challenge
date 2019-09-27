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

package adidascodingchallenge.consumerconsentsapp.converter;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;

public class CryptoConverter implements AttributeConverter<String, String> {
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] KEY = "TEwVnEJ1Vc8GXaZixBFkQZYdo10X0DXt".getBytes();
    
    @Override
    public String convertToDatabaseColumn(String attributeValue) {
        if (attributeValue != null) {
            Key key = new SecretKeySpec(KEY, "AES");
            
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                
                byte[] encryptedBytes = cipher.doFinal(attributeValue.getBytes());
                String encryptedBytesBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
                
                return encryptedBytesBase64;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                    IllegalBlockSizeException | BadPaddingException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return null;
        }
    }

    @Override
    public String convertToEntityAttribute(String databaseValue) {
        if (databaseValue != null) {
            Key key = new SecretKeySpec(KEY, "AES");
            
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, key);
                
                byte[] encryptedBytes = Base64.getDecoder().decode(databaseValue);
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                String decryptedValue = new String(decryptedBytes);
                
                return decryptedValue;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                    IllegalBlockSizeException | BadPaddingException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return null;
        }
    }
}