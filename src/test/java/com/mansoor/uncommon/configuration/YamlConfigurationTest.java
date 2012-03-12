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

package com.mansoor.uncommon.configuration;

import com.mansoor.uncommon.configuration.Convertors.Converter;
import com.mansoor.uncommon.configuration.Convertors.encryption.*;
import com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException;
import com.mansoor.uncommon.configuration.util.Preconditions;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Muhammad Ashraf
 * @since 2/25/12
 */
public class YamlConfigurationTest {
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new YamlConfiguration();
        configuration.load(new File(this.getClass().getResource("/test.yaml").getPath()));

        final KeyConfig symmetricKeyConfig = TestUtil.createSymmetricKeyConfig();
        final KeyConfig X509Config = TestUtil.createX509KeyConfig();

        final Converter<SymmetricKeyWrapper> symmetricKeyConverter = new SymmetricKeyConverter(symmetricKeyConfig);
        final Converter<X509Wrapper> x509CertConverter = new X509CertConverter(X509Config);

        configuration.getConverterRegistry().addConverter(SymmetricKeyWrapper.class, symmetricKeyConverter);
        configuration.getConverterRegistry().addConverter(X509Wrapper.class, x509CertConverter);
    }

    @Test
    public void testGetString() throws Exception {
        final String value = configuration.get(String.class, "hello");
        assertNotNull("value is null", value);
        assertEquals("value did not match the expected value", "world", value);
    }

    @Test(expected = ConverterNotFoundException.class)
    public void testConverterNotFoundException() throws Exception {
        configuration.get(URL.class, "name");
    }

    @Test
    public void testReload() throws Exception {
        configuration.set("abc", new File("abc"));
        Assert.assertNotNull(configuration.get(File.class, "abc"));
        configuration.reload();
        assertNull(configuration.get(File.class, "abc"));

    }

    @Test
    public void testSave() throws Exception {
        configuration.set("abc", new File("abc"));
        Assert.assertNotNull(configuration.get(File.class, "abc"));
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "newprop.yaml");
        Assert.assertNotNull(prop);
        assertTrue(prop.exists());
        final PropertyConfiguration newConfig = new PropertyConfiguration();
        newConfig.load(prop);
        final String value = newConfig.get(String.class, "abc");
        Assert.assertEquals("abc", value);
    }

    @Test
    public void testGetNestedValue() throws Exception {
        final String value = configuration.getNested(String.class, "development.adapter");
        assertNotNull(value);
        assertEquals("Values did not match", "mysql", value);
    }

    @Test
    public void testGetMapAsNestedValue() throws Exception {
        final String value = configuration.getNested(String.class, "development.password");
        assertNotNull(value);
    }

    @Test
    public void testGetInvalidNestedValue() throws Exception {
        final String value = configuration.getNested(String.class, "development.password.invalid");
        assertNull(value);
    }

    @Test
    public void testSingleValueAsNested() throws Exception {
        final String value = configuration.getNested(String.class, "dateOne");
        assertEquals("incorrect value", "02/23/2012", value);

    }

    @Test
    public void testGetNestedAsList() throws Exception {
        final List<File> files = configuration.getNestedList(File.class, "development.password.socket");
        assertFalse(Preconditions.isEmpty(files));
        assertTrue(files.size() == 2);
    }

    @Test
    public void testSetNested() throws Exception {
        configuration.setNested("production.database.admin.password", "12345");
        configuration.setNested("production.database.admin.userid", "admin");
        final Integer password = configuration.getNested(Integer.class, "production.database.admin.password");
        final String userId = configuration.getNested(String.class, "production.database.admin.userid");
        final String expectedUserId = "admin";
        final Integer expectedPassword = 12345;
        assertEquals("userid did not match", expectedUserId, userId);
        assertEquals("password did not match", expectedPassword, password);
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "newprop.yaml");
        assertTrue("file did not save", prop.exists());
    }

    @Test
    public void testGetYamlList() throws Exception {
        final String list = configuration.get(String.class, "list");
        assertNotNull(list);
    }


    @Test
    public void testEncryptedPasswordUsingSymmetricKey() throws Exception {
        final String plainPassword = configuration.getNested(String.class, "development.password.jms");
        assertThat(plainPassword, is(equalTo("secret jms password")));
        configuration.setNested("development.password.jms", new SymmetricKeyWrapper(plainPassword));
        final String encryptedPassword = configuration.getNested(String.class, "development.password.jms");
        assertThat(encryptedPassword, is(not(equalTo(plainPassword))));
        final SymmetricKeyWrapper decryptedPassword = configuration.getNested(SymmetricKeyWrapper.class, "development.password.jms");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "symmEncrypted.yaml");
        assertThat(prop, is(notNullValue()));
        assertTrue(prop.exists());
    }

    @Test
    public void testEncryptedPasswordUsingX509Cert() throws Exception {
        final String plainPassword = configuration.getNested(String.class, "development.password.database");
        assertThat(plainPassword, is(equalTo("secret database password")));
        configuration.setNested("development.password.database", new X509Wrapper(plainPassword));
        final String encryptedPassword = configuration.getNested(String.class, "development.password.database");
        assertThat(encryptedPassword, is(not(equalTo(plainPassword))));
        final X509Wrapper decryptedPassword = configuration.getNested(X509Wrapper.class, "development.password.database");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "x509Encrypted.yaml");
        assertThat(prop, is(notNullValue()));
        assertTrue(prop.exists());
    }

    @Test
    public void testGetX509EncryptedPasswordFromAFile() throws Exception {
        configuration.load(this.getClass().getResource("/x509Encrypted.yaml").getPath());
        final String plainPassword = "secret database password";
        final X509Wrapper decryptedPassword = configuration.getNested(X509Wrapper.class, "development.password.database");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));
    }

    @Test
    public void testGetSymmetricEncryptedPassword() throws Exception {
        configuration.load(this.getClass().getResource("/symmEncrypted.yaml").getPath());
        final String plainPassword = "secret jms password";
        final SymmetricKeyWrapper decryptedPassword = configuration.getNested(SymmetricKeyWrapper.class, "development.password.jms");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

    }
}
