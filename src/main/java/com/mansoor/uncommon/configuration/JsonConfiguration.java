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

import com.mansoor.uncommon.configuration.Convertors.ConverterRegistry;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Muhammad Ashraf
 * @since 3/4/12
 */
public class JsonConfiguration extends BaseConfiguration {
    protected JsonConfiguration(final ConverterRegistry converterRegistry) {
        super(converterRegistry);
    }

    protected void storeConfiguration(final File file) throws IOException {

    }

    protected void setProperty(final String key, final Object value) {

    }

    protected String getProperty(final String key) {
        return null;
    }

    protected void loadConfig(final File propertyFile) throws IOException {

    }

    protected void clearConfig() {

    }

    protected String getNestedValue(final String key) {
        return null;
    }

    public <E> List<E> getList(Class<E> type, String key) {
        return null;
    }

    public <E> List<E> getNestedList(Class<E> type, String key) {
        return null;
    }

    public <E> void setList(String key, List<E> input) {

    }

    public <E> void setList(String key, E... input) {

    }

    public <E> void setNested(final String key, final E input) {

    }

    public <E> void setNestedList(final String key, final List<E> input) {

    }

    public <E> void setNestedList(final String key, final E... input) {

    }
}
