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

import com.mansoor.uncommon.configuration.util.Preconditions;

import java.security.KeyStore;

/**
 * Configuration object encapsulating information required to load a KeyStore and a Key within that Store.
 * Client can either provide either a fully populated KeyStore or KeyStorePath, KeyStoreType and KeyStorePassword
 * to load key store from the file systme.
 *
 * <p>
 * To load a KeyStore from a file system, a KeyConfig will be populated as
 * <pre>
 *         new KeyConfig.Builder()
 *          .keyAlias("keyAlias")
 *          .keyPassword("keyPassword".toCharArray())
 *          .keyStorePassword("storePassword".toCharArray())
 *          .keyStoreType(EncryptionUtil.JCEKS)
 *          .keyStorePath("/path/to/store)
 *          .createKeyConfig();
 *  </pre>
 * </p>
 * <p>
 * To use an initialized KeyStore, configure the KeyConfig as
 *     <pre>
 *         new KeyConfig.Builder()
            .keyStore(keyStore)
            .keyAlias("uncommon-key")
            .keyPassword("password".toCharArray())
            .createKeyCofig();
 *     </pre>
 * </p>
 * @author Muhammad Ashraf
 * @since 0.1
 */
public class KeyConfig {
    /**
     * Key Store Path
     */
    private final String keyStorePath;
    /**
     * Key Store Path
     */
    private final char[] keyStorePassword;
    /**
     * Key Store Type
     */
    private final String keyStoreType;
    /**
     * Key Alias
     */
    private final String keyAlias;
    /**
     * Key Password
     */
    private final char[] keyPassword;
    /**
     * Key Store
     */
    private final KeyStore keyStore;

    private KeyConfig(final String keyStorePath, final char[] keyStorePassword, final String keyStoreType, final String keyAlias, final char[] keyPassword, final KeyStore keyStore) {
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.keyStoreType = keyStoreType;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword;
        this.keyStore = keyStore;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public char[] getKeyPassword() {
        return keyPassword;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public static class Builder {
        private String keyStorePath;
        private char[] keyStorePassword;
        private String keyStoreType;
        private String keyAlias;
        private char[] keyPassword;
        private KeyStore keyStore;

        public Builder keyStorePath(final String keyStorePath) {
            this.keyStorePath = keyStorePath;
            return this;
        }

        public Builder keyStorePassword(final char[] keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
            return this;
        }

        public Builder keyStoreType(final String keyStoreType) {
            this.keyStoreType = keyStoreType;
            return this;
        }

        public Builder keyAlias(final String keyAlias) {
            this.keyAlias = keyAlias;
            return this;
        }

        public Builder keyPassword(final char[] keyPassword) {
            this.keyPassword = keyPassword;
            return this;
        }

        public Builder keyStore(final KeyStore keyStore) {
            this.keyStore = keyStore;
            return this;
        }

        public KeyConfig createKeyCofig() {
            validate();
            return new KeyConfig(keyStorePath, keyStorePassword, keyStoreType, keyAlias, keyPassword, keyStore);
        }

        /**
         * Verifies that all required field are populated.
         */
        void validate() {
            Preconditions.checkBlank(keyAlias, "keyAlias is null");
            Preconditions.checkNull(keyPassword, "KeyPassword is null");
            Preconditions.checkArgument(keyStore != null || (keyStorePath != null && keyStoreType != null && keyPassword != null), "Either provide a KeyStore instance, or  key Store location, key store password and key store type ");

        }
    }
}
