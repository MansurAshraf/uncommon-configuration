/*
 * Copyright 2012. Muhammad M. Ashraf
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mansoor.uncommon.configuration.Convertors.encryption;

import com.mansoor.uncommon.configuration.Convertors.Converter;
import com.mansoor.uncommon.configuration.util.EncryptionUtil;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;

/**
 * @author Muhammad Ashraf
 * @since 3/11/12
 */


public class AsymmetricKeyEncryptionConverter extends EncryptionConverter implements Converter<ASDecryptString> {

    private final PublicKey publicKey;
    private final Key privateKey;
    private final Cipher cipher;

    public AsymmetricKeyEncryptionConverter(final KeyConfig config) {
        final KeyStore keyStore = getKeyStore(config);
        publicKey = EncryptionUtil.getPublicKey(keyStore, config.getKeyAlias());
        privateKey = EncryptionUtil.getPrivateKey(keyStore, config.getKeyAlias(), config.getKeyPassword());
        cipher = getCipher(EncryptionUtil.RSA_NONE_NO_PADDING);
    }


    /**
     * Converts a value to type T
     *
     * @param input value to be converted
     * @return converted value
     */
    public ASDecryptString convert(final String input) {
        final ASDecryptString decryptString;
        try {
            final byte[] encryptedString = Base64.decode(input.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            final byte[] decryptedBytes = cipher.doFinal(encryptedString);
            decryptString = new ASDecryptString(new String(decryptedBytes));
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
        return decryptString;

    }

    /**
     * Converts type T to String
     *
     * @param input input to be converted
     * @return String
     */
    public String toString(final ASDecryptString input) {
        final String enc;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            final byte[] cipherText = cipher.doFinal(input.getDecryptedText().getBytes());
            enc = new String(Base64.encode(cipherText));
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
        return enc;
    }
}
