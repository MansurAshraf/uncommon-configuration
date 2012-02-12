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

package com.mansoor.uncommon.configuration.Convertors;

import com.mansoor.uncommon.configuration.Configuration;
import com.mansoor.uncommon.configuration.PropertyConfiguration;
import com.mansoor.uncommon.configuration.exceptions.PropertyConversionException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Muhammad Ashraf
 * @since 2/11/12
 */
public class IntegerConverterTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {

        configuration = new PropertyConfiguration();
        final InputStream inputStream = this.getClass().getResourceAsStream("/testProp.properties");
        configuration.load(inputStream);
    }

    @Test
    public void testGetInteger() throws Exception {
        final Integer one = configuration.get(Integer.class, "one");
        assertNotNull("Null value returned!", one);
        final Integer expected = 1;
        assertEquals("Incorrect value returned", expected, one);
    }


    @org.junit.Test(expected = PropertyConversionException.class)
    public void testIntegerConversionException() throws Exception {
        configuration.get(Integer.class, "integerException");
        fail("expected conversion exception");
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    public void testCustomIntegerConverter() throws Exception {
        final Converter customConverter = mock(Converter.class);
        when(customConverter.convert("1")).thenReturn(2);
        final ConverterRegistry registry = new DefaultConverterRegistry();
        registry.addConverter(Integer.class, customConverter);

        final PropertyConfiguration propertyConfiguration = new PropertyConfiguration(registry);
        propertyConfiguration.load(new File(this.getClass().getResource("/testProp.properties").toURI()));

        final Integer one = propertyConfiguration.get(Integer.class, "one");
        assertNotNull("Null value returned!", one);
        final Integer expected = 2;
        assertEquals("Incorrect value returned", expected, one);
        verify(customConverter, times(1)).convert("1");
    }
}
