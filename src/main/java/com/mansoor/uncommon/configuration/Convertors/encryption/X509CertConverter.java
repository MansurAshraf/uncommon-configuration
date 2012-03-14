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
 * A simple X509 certificate based converter. This converter is configured to use a RSA/None/NoPadding
 * algorithm for encryption and decryption. A {@link com.mansoor.uncommon.configuration.Configuration}
 * implementation can be configured to use this converter by adding it to {@code Configuration's} converter
 * registry
 * <p/>
 * <pre>
 *   KeyConfig = new KeyConfig.Builder()
 *      .keyAlias("uncommon-key")
 *      .keyPassword("password".toCharArray())
 *      .keyStorePassword("password".toCharArray())
 *      .keyStorePath(/path/to/keystore)
 *      .keyStoreType(EncryptionUtil.JKS)
 *      .createKeyCofig();
 *
 *   Converter<X509Wrapper> x509CertConverter = new X509CertConverter(X509Config);
 *
 *   Configuration configuration = new YamlConfiguration();
 *   configuration.getConverterRegistry().addConverter(X509Wrapper.class, x509CertConverter);
 * </pre>
 *
 * @author Muhammad Ashraf
 * @since 0.1
 */


public class X509CertConverter extends EncryptionConverter implements Converter<X509Wrapper> {

    private final PublicKey publicKey;
    private final Key privateKey;
    private final Cipher cipher;

    public X509CertConverter(final KeyConfig config) {
        final KeyStore keyStore = getKeyStore(config);
        publicKey = EncryptionUtil.getPublicKey(keyStore, config.getKeyAlias());
        privateKey = EncryptionUtil.getPrivateKey(keyStore, config.getKeyAlias(), config.getKeyPassword());
        cipher = getCipher(EncryptionUtil.RSA_NONE_NO_PADDING);
    }


    /**
     * Decrypts the value using the private key
     *
     * @param input value to be decrypted
     * @return decrypted value
     */
    public X509Wrapper convert(final String input) {
        final X509Wrapper decryptString;
        try {
            final byte[] encryptedString = Base64.decode(input.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            final byte[] decryptedBytes = cipher.doFinal(encryptedString);
            decryptString = new X509Wrapper(new String(decryptedBytes));
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
        return decryptString;

    }

    /**
     * Encrypts the value using public key
     *
     * @param input value to be encrypted
     * @return encrypted String
     */
    public String toString(final X509Wrapper input) {
        final String enc;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            final byte[] cipherText = cipher.doFinal(input.getPlainText().getBytes());
            enc = new String(Base64.encode(cipherText));
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
        return enc;
    }

}
