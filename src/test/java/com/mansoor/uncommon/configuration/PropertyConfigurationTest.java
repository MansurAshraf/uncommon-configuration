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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author mansoor
 * @since 2/9/12
 */
public class PropertyConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = TestUtil.getPropertyConfiguration(("/testProp.properties"));
        final KeyConfig symmetricKeyConfig = TestUtil.createSymmetricKeyConfig();
        final KeyConfig X509Config = TestUtil.createX509KeyConfig();

        final Converter<SymmetricKeyWrapper> symmetricKeyConverter = new SymmetricKeyConverter(symmetricKeyConfig);
        final Converter<X509Wrapper> x509CertConverter = new X509CertConverter(X509Config);

        configuration.getConverterRegistry().addConverter(SymmetricKeyWrapper.class, symmetricKeyConverter);
        configuration.getConverterRegistry().addConverter(X509Wrapper.class, x509CertConverter);
    }

    @org.junit.Test(expected = ConverterNotFoundException.class)
    public void testConverterNotFoundException() throws Exception {
        configuration.get(URL.class, "name");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullConversionStrategy() throws Exception {
        configuration = new PropertyConfiguration(null);
        fail("Expected Illegal Argument Exception");
    }

    @Test(expected = IllegalStateException.class)
    public void testLoadFile() throws Exception {
        configuration.load(new File(""));
    }

    @Test
    public void testReload() throws Exception {
        configuration.set("abc", new File("abc"));
        assertNotNull(configuration.get(File.class, "abc"));
        configuration.reload();
        assertNull(configuration.get(File.class, "abc"));

    }

    @Test
    public void testSave() throws Exception {
        configuration.set("abc", new File("abc"));
        assertNotNull(configuration.get(File.class, "abc"));
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "newprop.properties");
        assertNotNull(prop);
        assertTrue(prop.exists());
        final PropertyConfiguration newConfig = new PropertyConfiguration();
        newConfig.load(prop);
        final String value = newConfig.get(String.class, "abc");
        assertEquals("abc", value);

    }

    @Test
    public void testGetNested() throws Exception {
        final String actual = configuration.getNested(String.class, "a.b.c");
        assertEquals("abc", actual);
    }

    //  @Test
    public void testEncryptedPasswordUsingSymmetricKey() throws Exception {
        final String plainPassword = configuration.getNested(String.class, "development.password.jms");
        assertThat(plainPassword, is(equalTo("secret jms password")));
        configuration.setNested("development.password.jms", new SymmetricKeyWrapper(plainPassword));
        final String encryptedPassword = configuration.getNested(String.class, "development.password.jms");
        assertThat(encryptedPassword, is(not(equalTo(plainPassword))));
        final SymmetricKeyWrapper decryptedPassword = configuration.getNested(SymmetricKeyWrapper.class, "development.password.jms");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "symmEncrypted.properties");
        assertThat(prop, is(notNullValue()));
        assertTrue(prop.exists());
    }

    //@Test
    public void testEncryptedPasswordUsingX509Cert() throws Exception {
        final String plainPassword = configuration.getNested(String.class, "development.password.database");
        assertThat(plainPassword, is(equalTo("secret database password")));
        configuration.setNested("development.password.database", new X509Wrapper(plainPassword));
        final String encryptedPassword = configuration.getNested(String.class, "development.password.database");
        assertThat(encryptedPassword, is(not(equalTo(plainPassword))));
        final X509Wrapper decryptedPassword = configuration.getNested(X509Wrapper.class, "development.password.database");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "x509Encrypted.properties");
        assertThat(prop, is(notNullValue()));
        assertTrue(prop.exists());
    }

    //@Test
    public void testGetX509EncryptedPasswordFromAFile() throws Exception {
        configuration.load(this.getClass().getResource("/x509Encrypted.properties").getPath());
        final String plainPassword = "secret database password";
        final X509Wrapper decryptedPassword = configuration.getNested(X509Wrapper.class, "development.password.database");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));
    }

    // @Test
    public void testGetSymmetricEncryptedPassword() throws Exception {
        configuration.load(this.getClass().getResource("/symmEncrypted.properties").getPath());
        final String plainPassword = "secret jms password";
        final SymmetricKeyWrapper decryptedPassword = configuration.getNested(SymmetricKeyWrapper.class, "development.password.jms");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

    }
}
