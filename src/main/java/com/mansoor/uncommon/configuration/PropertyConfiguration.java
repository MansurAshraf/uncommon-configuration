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
import com.mansoor.uncommon.configuration.Convertors.DefaultConverterRegistry;
import com.mansoor.uncommon.configuration.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * This Class is a type safe wrapper over {@link Properties} class and provides convenient methods to easily
 * manipulate Properties file
 *
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public class PropertyConfiguration extends BaseConfiguration {
    protected final Properties properties;
    private static final Logger log = LoggerFactory.getLogger(PropertyConfiguration.class);

    /**
     * Returns an instance of {@code PropertyConfiguration} that is configured to used
     * {@link DefaultConverterRegistry}
     */
    public PropertyConfiguration() {
        super(new DefaultConverterRegistry());
        properties = createProperties();
    }

    /**
     * Returns an instance of {@code PropertyConfiguration} configured with given Converter Registry
     *
     * @param converterRegistry registry that will be used by this PropertyConfiguration
     */
    public PropertyConfiguration(final ConverterRegistry converterRegistry) {
        super(converterRegistry);
        properties = createProperties();
    }


    public PropertyConfiguration(final ConverterRegistry converterRegistry, final long pollingRate, final TimeUnit timeUnit) {
        super(converterRegistry);
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        properties = createProperties();
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }

    public PropertyConfiguration(final long pollingRate, final TimeUnit timeUnit) {
        super(new DefaultConverterRegistry());
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        properties = createProperties();
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }

    protected Properties createProperties() {
        return new Properties();
    }

    protected String getProperty(final String key) {
        return properties.getProperty(key);
    }

    protected void setProperty(final String key, final String value) {
        properties.setProperty(key, value);
    }


    protected void loadConfig(final File propertyFile) throws IOException {
        properties.load(new FileInputStream(propertyFile));
    }

    protected void clearConfig() {
        properties.clear();
    }

    protected String getNestedValue(final String key) {
        return properties.getProperty(key);
    }


    protected void storeConfiguration(final File file) throws IOException {
        properties.store(new FileOutputStream(file), "");
    }


    public Properties toProperties() {
        lock.lock();
        try {
            final Properties p = new Properties();
            p.putAll(properties);
            return p;
        } finally {
            lock.unlock();
        }
    }


    class FilePoller implements Runnable {
        public void run() {
            log.info("Polling File");
            final File temp = new File(config.getAbsolutePath());
            if (temp.exists() && temp.lastModified() > lastModified) {
                lastModified = temp.lastModified();
                log.info("Reload Required");
                reload();
            } else {
                log.info("Not reloading file as no change has been detected since last load");
            }
        }
    }
}
