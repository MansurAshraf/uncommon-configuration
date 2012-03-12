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
 * @author Muhammad Ashraf
 * @since 3/10/12
 */
public class KeyConfig {
    private final String keyStorePath;
    private final char[] keyStorePassword;
    private final String keyStoreType;
    private final String keyAlias;
    private final char[] keyPassword;
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

        void validate() {
            Preconditions.checkBlank(keyAlias, "keyAlias is null");
            Preconditions.checkNull(keyPassword, "KeyPassword is null");
            Preconditions.checkArgument(keyStore != null || (keyStorePath != null && keyStoreType != null && keyPassword != null), "Either provide a KeyStore instance, or  key Store location, key store password and key store type ");

        }
    }
}
