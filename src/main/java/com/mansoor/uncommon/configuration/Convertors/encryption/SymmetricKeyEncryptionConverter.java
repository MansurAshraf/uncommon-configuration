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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyStore;
import java.security.Security;

/**
 * @author Muhammad Ashraf
 * @since 3/10/12
 */
public class SymmetricKeyEncryptionConverter implements Converter<SymmetricDecryptedString> {
    private final Cipher cipher;
    private final SecretKeySpec keySpec;

    public SymmetricKeyEncryptionConverter(final SymmetricKeyConfig config) throws Exception {
        loadProvider();
        Preconditions.checkNull(config, "config is null");
        final KeyStore keyStore = EncryptionUtil.loadKeyStore(config.getKeyStorePath(), config.getKeyStoreType(), config.getKeyStorePassword());
        this.keySpec = EncryptionUtil.getSecretKey(keyStore, config.getKeyAlias(), config.getKeyPassword());
        cipher = Cipher.getInstance("AES/ECB/NoPadding", EncryptionUtil.BC);

    }

    private void loadProvider() {
        if (Security.getProvider(EncryptionUtil.BC) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Converts a value to type T
     *
     * @param input value to be converted
     * @return converted value
     */
    public SymmetricDecryptedString convert(final String input) {
        byte[] cipherText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            final byte[] bytes = Base64.decode(input.getBytes());
            cipherText = new byte[bytes.length];
            int ctLength = cipher.update(bytes, 0, bytes.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);
        } catch (Exception e) {
            Throwables.propertyConversionException("encryption failed", e);
        }
        return new SymmetricDecryptedString(new String(cipherText));
    }

    /**
     * Converts type T to String
     *
     * @param input input to be converted
     * @return String
     */
    public String toString(final SymmetricDecryptedString input) {
        byte[] cipherText = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            final byte[] bytes = input.getDecryptedText().getBytes();
            cipherText = new byte[bytes.length];
            int ctLength = cipher.update(bytes, 0, bytes.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);
        } catch (Exception e) {
            Throwables.propertyConversionException("encryption failed", e);
        }
        return new String(Base64.encode(cipherText));
    }
}
