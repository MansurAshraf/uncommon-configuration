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
 * @author Muhammad Ashraf
 * @since 3/10/12
 */
public class SymmetricKeyEncryptionConverter implements Converter<SymmetricDecryptedString> {
    private final Cipher cipher;
    private final SecretKeySpec keySpec;

    public SymmetricKeyEncryptionConverter(final SymmetricKeyConfig config) throws Exception {
        Preconditions.checkNull(config, "config is null");
        final KeyStore keyStore = getKeyStore(config);
        this.keySpec = EncryptionUtil.getSecretKey(keyStore, config.getKeyAlias(), config.getKeyPassword());
        cipher = Cipher.getInstance(EncryptionUtil.AES_CBC_PKCS7_PADDING, EncryptionUtil.BC);

    }

    private KeyStore getKeyStore(final SymmetricKeyConfig config) {
        return config.getKeyStore() == null ? EncryptionUtil.loadKeyStore(config.getKeyStorePath(), config.getKeyStoreType(), config.getKeyStorePassword()) : config.getKeyStore();
    }

    /**
     * Converts a value to type T
     *
     * @param input value to be converted
     * @return converted value
     */
    public SymmetricDecryptedString convert(final String input) {

        SymmetricDecryptedString symmetricDecryptedString = null;
        try {
            final IvParameterSpec ips = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ips);
            final byte[] bytes = Base64.decode(input.getBytes());
            final byte[] cipherText = cipher.doFinal(bytes);
            symmetricDecryptedString = new SymmetricDecryptedString(new String(cipherText));
        } catch (Exception e) {
            Throwables.propertyConversionException("encryption failed", e);
        }
        return symmetricDecryptedString;
    }

    /**
     * Converts type T to String
     *
     * @param input input to be converted
     * @return String
     */
    public String toString(final SymmetricDecryptedString input) {
        String enc = null;
        try {
            final IvParameterSpec ips = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ips);
            final byte[] bytes = input.getDecryptedText().getBytes();
            final byte[] cipherText = cipher.doFinal(bytes);
            enc = new String(Base64.encode(cipherText));
        } catch (Exception e) {
            Throwables.propertyConversionException("encryption failed", e);
        }
        return enc;
    }
}
