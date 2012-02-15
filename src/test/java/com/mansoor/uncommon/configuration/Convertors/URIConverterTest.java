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

import java.net.URI;

/**
 * @author Muhammad Ashraf
 * @since 2/14/12
 */
public class URIConverterTest {
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {

        configuration = TestUtil.getPropertyConfiguration("/testProp.properties");
    }

    @Test
    public void testGETUri() throws Exception {
        final URI result = configuration.get(URI.class, "uri1");
        Assert.assertEquals("uri", URI.create("www.google.com"), result);
    }

    @Test
    public void testSetUri() throws Exception {
        final URI expected = URI.create("www.yahoo.com");
        configuration.set(URI.class, "uri2", expected);
        final URI result = configuration.get(URI.class, "uri2");
        Assert.assertEquals("results did not match", expected, result);

    }
}
