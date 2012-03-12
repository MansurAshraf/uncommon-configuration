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
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author Muhammad Ashraf
 * @since 3/9/12
 */
public class JsonConfigurationTest {
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        final KeyConfig symmetricKeyConfig = TestUtil.createSymmetricKeyConfig();
        final KeyConfig X509Config = TestUtil.createX509KeyConfig();

        final Converter<SymmetricKeyWrapper> symmetricKeyConverter = new SymmetricKeyConverter(symmetricKeyConfig);
        final Converter<X509Wrapper> x509CertConverter = new X509CertConverter(X509Config);

        configuration = TestUtil.getJsonConfiguration("/test.json");
        configuration.getConverterRegistry().addConverter(SymmetricKeyWrapper.class, symmetricKeyConverter);
        configuration.getConverterRegistry().addConverter(X509Wrapper.class, x509CertConverter);
    }

    @Test
    public void testGet() throws Exception {
        final String value = configuration.get(String.class, "test");
        assertThat(value, equalTo("test String"));
    }

    @Test
    public void testGetNested() throws Exception {
        final String nestedValue = configuration.getNested(String.class, "glossary.title");
        assertThat(nestedValue, equalTo("example glossary"));
    }

    @Test
    public void testGetNestedList() throws Exception {
        final List<String> list = configuration.getNestedList(String.class, "glossary.GlossDiv.GlossList.GlossEntry.GlossDef.GlossSeeAlso");
        assertThat(list, hasItems("GML", "XML"));

    }

    @Test
    public void testGetNonExistentValue() throws Exception {
        final String value = configuration.get(String.class, "does not exist");
        assertThat(value, is(nullValue()));
    }

    @Test
    public void testGetNonExistentNestedValue() throws Exception {
        final String value = configuration.getNested(String.class, "does not exist");
        assertThat(value, is(nullValue()));
    }

    @Test
    public void testGetNonExistentListValue() throws Exception {
        final List<String> value = configuration.getList(String.class, "does not exist");
        assertThat(value, is(nullValue()));
    }

    @Test
    public void testGetNonExistenNestedtListValue() throws Exception {
        final List<String> value = configuration.getNestedList(String.class, "does not exist");
        assertThat(value, is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingNullList() throws Exception {
        final List<String> list = null;
        configuration.setList("abc", list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingNullValue() throws Exception {
        configuration.set("abc", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingNullNestedValue() throws Exception {
        configuration.setNested("abc", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSettingNullNestedList() throws Exception {
        final List<String> value = null;
        configuration.setNestedList("abc", value);
    }

    @Test
    public void testGettingNonNestedValueWithNestMethod() throws Exception {
        final String result = configuration.getNested(String.class, "test");
        assertThat(result, is(equalTo("test String")));
    }

    @Test
    public void testGettingListWithNonListMethod() throws Exception {
        final String value = configuration.get(String.class, "testList");
        assertThat(value, is(notNullValue()));

    }

    @Test
    public void testSaveList() throws Exception {
        final List<File> files = Arrays.asList(new File("/tmp/f1.txt"), new File("/temp/file2"));
        configuration.setList("files", files);
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "newjson.json");
        Assert.assertNotNull(prop);
        assertTrue(prop.exists());
    }

    @Test
    public void testSetValueInAnExistingKey() throws Exception {
        configuration.setNested("glossary.newtitle", "New Title");
        final String value = configuration.getNested(String.class, "glossary.newtitle");
        assertThat(value, is(equalTo("New Title")));
    }

    @Test
    public void testEncryptedPasswordUsingSymmetricKey() throws Exception {
        final String plainPassword = configuration.getNested(String.class, "glossary.GlossDiv.GlossList.GlossEntry.Password");
        assertThat(plainPassword, is(equalTo("super secret password")));
        configuration.setNested("glossary.GlossDiv.GlossList.GlossEntry.Password", new SymmetricKeyWrapper(plainPassword));
        final String encryptedPassword = configuration.getNested(String.class, "glossary.GlossDiv.GlossList.GlossEntry.Password");
        assertThat(encryptedPassword, is(not(equalTo(plainPassword))));
        final SymmetricKeyWrapper decryptedPassword = configuration.getNested(SymmetricKeyWrapper.class, "glossary.GlossDiv.GlossList.GlossEntry.Password");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "symmEncrypted.json");
        assertThat(prop, is(notNullValue()));
        assertTrue(prop.exists());
    }

    @Test
    public void testEncryptedPasswordUsingX509Cert() throws Exception {
        final String plainPassword = configuration.getNested(String.class, "glossary.GlossDiv.GlossList.GlossEntry.MasterPassword");
        assertThat(plainPassword, is(equalTo("super secret master password")));
        configuration.setNested("glossary.GlossDiv.GlossList.GlossEntry.MasterPassword", new X509Wrapper(plainPassword));
        final String encryptedPassword = configuration.getNested(String.class, "glossary.GlossDiv.GlossList.GlossEntry.MasterPassword");
        assertThat(encryptedPassword, is(not(equalTo(plainPassword))));
        final X509Wrapper decryptedPassword = configuration.getNested(X509Wrapper.class, "glossary.GlossDiv.GlossList.GlossEntry.MasterPassword");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "x509Encrypted.json");
        assertThat(prop, is(notNullValue()));
        assertTrue(prop.exists());
    }

    @Test
    public void testGetX509EncryptedPasswordFromAFile() throws Exception {
        configuration.load(this.getClass().getResource("/x509Encrypted.json").getPath());
        final String plainPassword = "super secret master password";
        final X509Wrapper decryptedPassword = configuration.getNested(X509Wrapper.class, "glossary.GlossDiv.GlossList.GlossEntry.MasterPassword");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));
    }

    @Test
    public void testGetSymmetricEncryptedPassword() throws Exception {
        configuration.load(this.getClass().getResource("/symmEncrypted.json").getPath());
        final String plainPassword = "super secret password";
        final SymmetricKeyWrapper decryptedPassword = configuration.getNested(SymmetricKeyWrapper.class, "glossary.GlossDiv.GlossList.GlossEntry.Password");
        assertThat(decryptedPassword.getPlainText(), is(equalTo(plainPassword)));

    }
}
