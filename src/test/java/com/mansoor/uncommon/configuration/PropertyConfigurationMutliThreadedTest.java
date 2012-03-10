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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PropertyConfigurationMutliThreadedTest {
    private Configuration configuration;
    private File file;

    @Before
    public void setUp() throws Exception {

        configuration = new PropertyConfiguration(5, TimeUnit.SECONDS);

        file = new File(this.getClass().getResource("/testProp.properties").getPath());
        configuration.load(file);
    }

    @Test(timeout = 10 * 1000)//10 seconds
    public void testReload() throws Exception {
        configuration.set("newKey", "newKey");
        String value = configuration.get(String.class, "newKey");
        final boolean result = file.setLastModified(new Date().getTime());
        Assert.assertTrue("last modified update failed", result);
        while (value != null) {
            value = configuration.get(String.class, "newKey");
        }
    }
}
