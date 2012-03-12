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

package com.mansoor.uncommon.configuration.Convertors.encryption;

import com.mansoor.uncommon.configuration.Convertors.Converter;
import com.mansoor.uncommon.configuration.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Muhammad Ashraf
 * @since 3/10/12
 */
public class SymmetricKeyConverterTest {

    private Converter<SymmetricKeyWrapper> converter;

    @Before
    public void setUp() throws Exception {
        final KeyConfig config = TestUtil.createSymmetricKeyConfig();
        converter = new SymmetricKeyConverter(config);
    }

    @Test
    public void testEncryption() throws Exception {
        final String plainText = "World Domination master plan";
        final String encryptedString = converter.toString(new SymmetricKeyWrapper(plainText));
        assertThat(encryptedString, is(notNullValue()));
        final SymmetricKeyWrapper SymmetricKeyWrapper = converter.convert(encryptedString);
        assertThat(SymmetricKeyWrapper.getPlainText(), is(equalTo(plainText)));
    }
}
