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

import com.mansoor.uncommon.configuration.Exceptions.ConverterNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author mansoor
 * @since 2/9/12
 */
public class PropertyConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = new PropertyConfiguration();
        configuration.load(this.getClass().getResourceAsStream("/testProp.properties"));
    }

    @org.junit.Test(expected = ConverterNotFoundException.class)
    public void testConverterNotFoundException() throws Exception {
        final String name = configuration.get(String.class, "name");
    }

    @Test
    public void testGetInteger() throws Exception {
        final Integer one = configuration.get(Integer.class, "one");
        assertNotNull("Null value returned!", one);
        final Integer expected = 1;
        assertEquals("Incorrect value returned", expected, one);
    }

    @After
    public void tearDown() throws Exception {

    }
}
