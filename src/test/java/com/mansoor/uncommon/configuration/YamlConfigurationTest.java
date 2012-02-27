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

import com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException;
import com.mansoor.uncommon.configuration.util.Preconditions;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.*;
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
        final List<File> files = configuration.getNestedAsList(File.class, "development.password.socket");
        assertFalse(Preconditions.isEmpty(files));
        assertTrue(files.size() == 2);
    }
}
