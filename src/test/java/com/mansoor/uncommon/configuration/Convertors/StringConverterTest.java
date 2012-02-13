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
import com.mansoor.uncommon.configuration.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Muhammad Ashraf
 * @since 2/12/12
 */
public class StringConverterTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = TestUtil.getPropertyConfiguration("/testProp.properties");
    }

    @Test
    public void testValueWithSpace() throws Exception {
        final String result = configuration.get(String.class, "stringOne");
        Assert.assertEquals("incorrect value", "one two", result);
    }
}
