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

package com.mansoor.uncommon.configuration.util;

import org.junit.Test;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.KeyStore;

import static org.junit.Assert.assertTrue;

/**
 * @author Muhammad Ashraf
 * @since 3/10/12
 */
public class EncryptionUtilTest {


    @Test
    public void testStoreKeyStore() throws Exception {
        final KeyStore keyStore = EncryptionUtil.createKeyStore(EncryptionUtil.JCEKS);
        final SecretKeySpec key = EncryptionUtil.createSecretAESKey();
        EncryptionUtil.storeSecretKey(keyStore, key, "123456789".toCharArray(), "secret");
        assertTrue(keyStore.containsAlias("secret"));
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final String path = tempLocation + File.separator + "keyStore.jceks";
        EncryptionUtil.saveKeyStore(keyStore, "password".toCharArray(), path);
        assertTrue(new File(path).exists());
    }

    @Test
    public void testLoadKeyStore() throws Exception {
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final String path = tempLocation + File.separator + "keyStore.jceks";
        final KeyStore keyStore = EncryptionUtil.loadKeyStore(path, EncryptionUtil.JCEKS, "password".toCharArray());
        assertTrue(keyStore.containsAlias("secret"));
    }
}
