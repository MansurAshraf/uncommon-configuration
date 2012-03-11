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

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;

/**
 * @author Muhammad Ashraf
 * @since 3/10/12
 */
public class EncryptionUtil {

    public static final String AES = "AES";
    public static final String JCEKS = "JCEKS";
    public static final String BC = "BC";
    public static final String AES_CBC_PKCS7_PADDING = "AES/CBC/PKCS7Padding";

    static {
        loadProvider();
    }

    private EncryptionUtil() {

    }

    public static SecretKey createSecretAESKey() {
        final KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance(EncryptionUtil.AES, EncryptionUtil.BC);
            generator.init(256, new SecureRandom());
        } catch (Exception e) {
            throw new IllegalStateException("unable to generate key", e);
        }
        return generator.generateKey();
    }

    public static SecretKey createSecretKey(final int keySize, final String algorithm) {
        final KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance(algorithm, EncryptionUtil.BC);
            generator.init(keySize, new SecureRandom());
        } catch (Exception e) {
            throw new IllegalStateException("unable to generate key", e);
        }
        return generator.generateKey();
    }

    public static KeyStore createKeyStore(final String storeType) {
        Preconditions.checkBlank(storeType, "Store type is null or empty");
        final KeyStore store;
        try {
            store = KeyStore.getInstance(storeType);
            store.load(null, null);
        } catch (Exception e) {
            throw new IllegalStateException("unable to create Store", e);
        }
        return store;
    }

    public static void storeSecretKey(final KeyStore store, final SecretKey key, final char[] keyPassword, final String keyAlias) {
        Preconditions.checkNull(store, "store is null");
        Preconditions.checkNull(key, "key is null");
        Preconditions.checkNull(keyPassword, "password is null");
        Preconditions.checkBlank(keyAlias, "Alias is null or empty");
        try {
            store.setEntry(keyAlias, new KeyStore.SecretKeyEntry(key), new KeyStore.PasswordProtection(keyPassword));
        } catch (KeyStoreException e) {
            throw new IllegalStateException("unable to store key", e);
        }
    }

    public static void saveKeyStore(final KeyStore keyStore, final char[] keyStorePassword, final String path) {
        Preconditions.checkNull(keyStore, "store is null");
        Preconditions.checkNull(keyStorePassword, "password is null");
        Preconditions.checkBlank(path, "path is null");
        try {
            keyStore.store(new FileOutputStream(new File(path)), keyStorePassword);
        } catch (Exception e) {
            throw new IllegalStateException("unable to save key store", e);
        }
    }

    public static KeyStore loadKeyStore(final String path, final String keyStoreType, final char[] password) {
        Preconditions.checkBlank(path, "path is null or empty");
        Preconditions.checkBlank(keyStoreType, "keyStoreType is null or empty");
        Preconditions.checkArgument(password != null && password.length > 0, "password is null or empty");
        final KeyStore store;
        try {
            store = KeyStore.getInstance(keyStoreType);
            store.load(new FileInputStream(path), password);
        } catch (Exception e) {
            throw new IllegalStateException("unable to create Store", e);
        }
        return store;
    }

    public static SecretKeySpec getSecretKey(final KeyStore store, final String keyAlias, final char[] keyPassword) {
        Preconditions.checkNull(store, "store is null");
        Preconditions.checkArgument(keyPassword != null && keyPassword.length > 0, "password is null or empty");
        Preconditions.checkBlank(keyAlias, "Alias is null or empty");
        final Key key;
        try {
            key = store.getKey(keyAlias, keyPassword);
        } catch (Exception e) {
            throw new IllegalStateException("unable to create Store", e);
        }
        return (SecretKeySpec) key;
    }

    private static void loadProvider() {
        if (Security.getProvider(EncryptionUtil.BC) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}
