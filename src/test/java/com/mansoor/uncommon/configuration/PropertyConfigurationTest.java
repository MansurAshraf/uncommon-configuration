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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static junit.framework.Assert.*;

/**
 * @author mansoor
 * @since 2/9/12
 */
public class PropertyConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = TestUtil.getPropertyConfiguration(("/testProp.properties"));
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
}
