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
import com.mansoor.uncommon.configuration.util.Preconditions;
import com.mansoor.uncommon.configuration.util.Throwables;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyStore;

/**
 * A simple Symmetric Key based converter. This converter is configured to use a AES/CBC/PKCS7Padding
 * algorithm for encryption and decryption. A {@link com.mansoor.uncommon.configuration.Configuration}
 * implementation can be configured to use this converter by adding it to {@code Configuration's} converter
 * registry
 * <p/>
 * <pre>
 *   KeyConfig = new KeyConfig.Builder()
 *          .keyAlias("keyAlias")
 *          .keyPassword("keyPassword".toCharArray())
 *          .keyStorePassword("storePassword".toCharArray())
 *          .keyStoreType(EncryptionUtil.JCEKS)
 *          .keyStorePath("/path/to/store)
 *          .createKeyConfig();
 *
 *   Converter<SymmetricKeyWrapper> symmetricKeyConverter = new SymmetricKeyConverter(symmetricKeyConfig);
 *
 *   Configuration configuration = new YamlConfiguration();
 *   configuration.getConverterRegistry().addConverter(SymmetricKeyWrapper.class, symmetricKeyConverter);
 * </pre>
 *
 * @author Muhammad Ashraf
 * @since 0.1
 */
public class SymmetricKeyConverter extends EncryptionConverter implements Converter<SymmetricKeyWrapper> {
    private final Cipher cipher;
    private final SecretKeySpec keySpec;

    public SymmetricKeyConverter(final KeyConfig config) {
        Preconditions.checkNull(config, "config is null");
        final KeyStore keyStore = getKeyStore(config);
        this.keySpec = (SecretKeySpec) EncryptionUtil.getSecretKey(keyStore, config.getKeyAlias(), config.getKeyPassword());
        cipher = getCipher(EncryptionUtil.AES_CBC_PKCS7_PADDING);

    }

    /**
     * Decrypts the input String using Symmetric Key.
     *
     * @param input encrypted value
     * @return instance of SymmetricKeyWrapper containing decrypted value
     */
    public SymmetricKeyWrapper convert(final String input) {

        SymmetricKeyWrapper SymmetricKeyWrapper = null;
        try {
            final IvParameterSpec ips = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ips);
            final byte[] bytes = Base64.decode(input.getBytes());
            final byte[] cipherText = cipher.doFinal(bytes);
            SymmetricKeyWrapper = new SymmetricKeyWrapper(new String(cipherText));
        } catch (Exception e) {
            Throwables.propertyConversionException("encryption failed", e);
        }
        return SymmetricKeyWrapper;
    }

    /**
     * Encrypts the input value using Symmetric Key.
     *
     * @param input input to be encrypted
     * @return encrypted String
     */
    public String toString(final SymmetricKeyWrapper input) {
        String enc = null;
        try {
            final IvParameterSpec ips = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ips);
            final byte[] bytes = input.getPlainText().getBytes();
            final byte[] cipherText = cipher.doFinal(bytes);
            enc = new String(Base64.encode(cipherText));
        } catch (Exception e) {
            Throwables.propertyConversionException("encryption failed", e);
        }
        return enc;
    }

}
