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

import static junit.framework.Assert.fail;

/**
 * @author mansoor
 * @since 2/9/12
 */
public class PropertyConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new PropertyConfiguration();
        configuration.load(("/testProp.properties"));
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
}
