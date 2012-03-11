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

/**
 * @author Muhammad Ashraf
 * @since 3/10/12
 */
public class SymmetricKeyConfig {
    private final String keyStorePath;
    private final char[] keyStorePassword;
    private final String keyStoreType;
    private final String keyAlias;
    private final char[] keyPassword;

    private SymmetricKeyConfig(final String keyStorePath, final char[] keyStorePassword, final String keyStoreType, final String keyAlias, final char[] keyPassword) {
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.keyStoreType = keyStoreType;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword;
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

    public static class SymmetricKeyConfigBuilder {
        private String keyStorePath;
        private char[] keyStorePassword;
        private String keyStoreType;
        private String keyAlias;
        private char[] keyPassword;

        public SymmetricKeyConfigBuilder keyStorePath(final String keyStorePath) {
            this.keyStorePath = keyStorePath;
            return this;
        }

        public SymmetricKeyConfigBuilder keyStorePassword(final char[] keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
            return this;
        }

        public SymmetricKeyConfigBuilder keyStoreType(final String keyStoreType) {
            this.keyStoreType = keyStoreType;
            return this;
        }

        public SymmetricKeyConfigBuilder keyAlias(final String keyAlias) {
            this.keyAlias = keyAlias;
            return this;
        }

        public SymmetricKeyConfigBuilder keyPassword(final char[] keyPassword) {
            this.keyPassword = keyPassword;
            return this;
        }

        public SymmetricKeyConfig createSymmetricKeyCofig() {
            validate();
            return new SymmetricKeyConfig(keyStorePath, keyStorePassword, keyStoreType, keyAlias, keyPassword);
        }

        void validate() {
            Preconditions.checkBlank(keyAlias, "keyAlias is null");
            Preconditions.checkNull(keyPassword, "KeyPassword is null");
            Preconditions.checkBlank(keyStorePath, "path is null");
            Preconditions.checkBlank(keyStoreType, "key store type is null");
            Preconditions.checkNull(keyStorePassword, "key store password is null");
        }
    }
}
