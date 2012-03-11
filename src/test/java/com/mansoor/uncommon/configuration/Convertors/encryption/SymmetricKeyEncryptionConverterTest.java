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
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.KeyStore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Muhammad Ashraf
 * @since 3/10/12
 */
public class SymmetricKeyEncryptionConverterTest {

    private final char[] keyPassword = "123456789".toCharArray();
    private final char[] keyStorePassword = "password".toCharArray();
    private Converter<SDecryptString> converter;

    @Before
    public void setUp() throws Exception {
        final KeyStore keyStore = EncryptionUtil.createKeyStore(EncryptionUtil.JCEKS);
        final SecretKey key = EncryptionUtil.createSecretAESKey();
        EncryptionUtil.storeSecretKey(keyStore, key, keyPassword, "secret");
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final String path = tempLocation + File.separator + "keyStore.jceks";
        EncryptionUtil.saveKeyStore(keyStore, keyStorePassword, path);
        final KeyConfig config = new KeyConfig.Builder()
                .keyAlias("secret")
                .keyPassword(keyPassword)
                .keyStorePassword(keyStorePassword)
                .keyStoreType(EncryptionUtil.JCEKS)
                .keyStorePath(path)
                .createSymmetricKeyCofig();
        converter = new SymmetricKeyEncryptionConverter(config);
    }

    @Test
    public void testEncryption() throws Exception {
        final String plainText = "World Domination master plan";
        final String encryptedString = converter.toString(new SDecryptString(plainText));
        assertThat(encryptedString, is(notNullValue()));
        final SDecryptString SDecryptString = converter.convert(encryptedString);
        assertThat(SDecryptString.getDecryptedText(), is(equalTo(plainText)));
    }
}
