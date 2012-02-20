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

import com.mansoor.uncommon.configuration.util.Preconditions;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Muhammad Ashraf
 * @since 2/19/12
 */
public class SystemPropertyConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new SystemPropertyConfiguration();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLoad() throws Exception {
        configuration.load(new File(""));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLoadWithPath() throws Exception {
        configuration.load((""));
    }

    @Test
    public void testSave() throws Exception {
        configuration.set("abc", new File("abc"));
        assertNotNull(configuration.get(File.class, "abc"));
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "sysprop.properties");
        assertNotNull(prop);
        assertTrue(prop.exists());
        final PropertyConfiguration newConfig = new PropertyConfiguration();
        newConfig.load(prop);
        final String value = newConfig.get(String.class, "abc");
        assertEquals("abc", value);
    }

    @Test
    public void testGetUri() throws Exception {
        final URI bugTracker = configuration.get(URI.class, "java.vendor.url.bug");
        assertNotNull(bugTracker);
    }

    @Test
    public void testGetFile() throws Exception {
        final File userDir = configuration.get(File.class, "user.dir");
        assertNotNull(userDir);
        assertTrue(userDir.isDirectory());
    }

    @Test
    public void testGetList() throws Exception {

        ((SystemPropertyConfiguration) configuration).setDeliminator(':');
        final List<File> classpath = configuration.getList(File.class, "java.class.path");
        assertTrue(Preconditions.isNotEmpty(classpath));
    }
}
