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
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Muhammad Ashraf
 * @since 2/25/12
 */
public class YamlConfiguration extends BaseConfiguration {
    private final Map<String, Object> properties;
    private static final Logger log = LoggerFactory.getLogger(YamlConfiguration.class);

    protected YamlConfiguration(final ConverterRegistry converterRegistry) {
        super(converterRegistry);
        properties = new HashMap<String, Object>();
    }

    protected YamlConfiguration() {
        super(new DefaultConverterRegistry());
        properties = new HashMap<String, Object>();
    }

    public YamlConfiguration(final ConverterRegistry converterRegistry, final long pollingRate, final TimeUnit timeUnit) {
        super(converterRegistry);
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        properties = new HashMap<String, Object>();
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }

    public YamlConfiguration(final long pollingRate, final TimeUnit timeUnit) {
        super(new DefaultConverterRegistry());
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        properties = new HashMap<String, Object>();
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }

    protected void setProperty(final String key, final String value) {
        lock.lock();
        try {
            properties.put(key, value);
        } finally {
            lock.unlock();
        }
    }


    protected String getProperty(final String key) {
        String result = null;
        final Object value = properties.get(key);
        if (Preconditions.isNotNull(value)) {
            if (String.class.isAssignableFrom(value.getClass())) {
                result = (String) value;
            } else {
                result = value.toString();
            }
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    protected void loadConfig(final File propertyFile) throws IOException {
        final Yaml yaml = new Yaml();
        final Object data = yaml.load(new FileInputStream(propertyFile));
        properties.putAll((Map<String, Object>) data);
    }

    protected void clearConfig() {
        properties.clear();
    }

    protected void storeConfiguration(final File file) throws IOException {

    }

    public void clear() {

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
