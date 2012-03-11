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
 * @author Muhammad Ashraf
 * @since 3/11/12
 */
public abstract class EncryptionConverter {

    KeyStore getKeyStore(final KeyConfig config) {
        return config.getKeyStore() == null ? EncryptionUtil.loadKeyStore(config.getKeyStorePath(), config.getKeyStoreType(), config.getKeyStorePassword()) : config.getKeyStore();
    }

    Cipher getCipher(final String algo) {
        Cipher instance = null;
        try {
            instance = Cipher.getInstance(algo, EncryptionUtil.BC);
        } catch (Exception e) {
            throw new IllegalStateException("unable to get cipher", e);
        }
        return instance;
    }
}
