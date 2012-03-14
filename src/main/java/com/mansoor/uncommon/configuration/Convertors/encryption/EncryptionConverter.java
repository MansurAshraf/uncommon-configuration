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

import com.mansoor.uncommon.configuration.util.EncryptionUtil;

import javax.crypto.Cipher;
import java.security.KeyStore;

/**
 * Provides common encryption related methods
 * @author Muhammad Ashraf
 * @since 0.1
 */
public abstract class EncryptionConverter {

    /**
     * Returns an instance of {@link KeyStore} based on information provided in {@code KeyConfig}. If {@code KeyConfig} contains an instance
     * of {@code KeyStore}, it will be returned from this method. Otherwise KeyStorePath, KeyStoreType and KeyStorePassword fields will
     * be used to load a new instance of KeyStore from the file system.
     * @param config KeyConfig containing information required to load the key store
     * @return KeyStore
     * @throws IllegalStateException if loading fails
     */
    KeyStore getKeyStore(final KeyConfig config) {
        return config.getKeyStore() == null ? EncryptionUtil.loadKeyStore(config.getKeyStorePath(), config.getKeyStoreType(), config.getKeyStorePassword()) : config.getKeyStore();
    }

    /**
     * Returns a {@link Cipher} configured with provided algorithm.
     * @param algorithm algorithm that will be used by the Cipger
     * @return Cipher
     */
    Cipher getCipher(final String algorithm) {
        Cipher instance = null;
        try {
            instance = Cipher.getInstance(algorithm, EncryptionUtil.BC);
        } catch (Exception e) {
            throw new IllegalStateException("unable to get cipher", e);
        }
        return instance;
    }
}
