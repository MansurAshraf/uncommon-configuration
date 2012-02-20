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
import java.util.Properties;

/**
 * @author Muhammad Ashraf
 * @since 2/19/12
 */
public class SystemPropertyConfiguration extends PropertyConfiguration {

    /**
     * Returns an instance of {@code PropertyConfiguration} that is configured to used
     * {@link com.mansoor.uncommon.configuration.Convertors.DefaultConverterRegistry}
     */
    public SystemPropertyConfiguration() {
        super();
    }

    /**
     * Returns an instance of {@code PropertyConfiguration} configured with given Converter Registry
     *
     * @param converterRegistry registry that will be used by this PropertyConfiguration
     */
    public SystemPropertyConfiguration(final ConverterRegistry converterRegistry) {
        super(converterRegistry);
    }

    /**
     * Loads the given property file
     *
     * @param propertyFile property file
     */
    public void load(final File propertyFile) {
        throw new UnsupportedOperationException("Operation not supported on SystemPropertyConfiguration");
    }

    /**
     * Loads the property file associated with the given input stream
     *
     * @param path file path
     */
    public void load(final String path) {
        throw new UnsupportedOperationException("Operation not supported on SystemPropertyConfiguration");
    }


    public void reload() {
        lock.lock();
        try {
            properties.clear();
            final Properties p = System.getProperties();
            properties.putAll(p);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stops file polling
     */
    public void stopPolling() {
        throw new UnsupportedOperationException("Operation not supported on SystemPropertyConfiguration");
    }

    Properties createProperties() {
        final Properties p = new Properties();
        p.putAll(System.getProperties());
        return p;
    }
}
