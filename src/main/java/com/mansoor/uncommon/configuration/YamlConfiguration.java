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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for accessing and manipulating {@code YAML} configuration.
 *
 * @author Muhammad Ashraf
 * @since 3/4/12
 */
public class YamlConfiguration extends MapBasedConfiguration {
    private static final Logger log = LoggerFactory.getLogger(YamlConfiguration.class);

    /**
     * Returns an instance of {@code YamlConfiguration} configured with given Converter Registry
     *
     * @param converterRegistry registry that will be used by this PropertyConfiguration
     */
    protected YamlConfiguration(final ConverterRegistry converterRegistry) {
        super(converterRegistry, new HashMap<String, Object>());
    }

    /**
     * Returns an instance of {@code YamlConfiguration} that is configured to use
     * {@link DefaultConverterRegistry}
     */
    protected YamlConfiguration() {
        super(new DefaultConverterRegistry(), new HashMap<String, Object>());
    }

    /**
     * Returns an instance of {@code YamlConfiguration} that is configured to poll configuration file for change
     *
     * @param converterRegistry registry that will be used by this PropertyConfiguration
     * @param pollingRate       polling rate
     * @param timeUnit          time unit (eg: seconds, minute etc)
     */
    public YamlConfiguration(final ConverterRegistry converterRegistry, final long pollingRate, final TimeUnit timeUnit) {
        super(converterRegistry, new HashMap<String, Object>());
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }

    /**
     * Returns an instance of {@code YamlConfiguration} that is configured to poll configuration file for change
     *
     * @param pollingRate polling rate
     * @param timeUnit    time unit (eg: seconds, minute etc)
     */
    public YamlConfiguration(final long pollingRate, final TimeUnit timeUnit) {
        super(new DefaultConverterRegistry(), new HashMap<String, Object>());
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected void loadConfig(final File propertyFile) throws IOException {
        log.debug("loading file '()'" + propertyFile.getPath());
        final Yaml yaml = new Yaml();
        final Object data = yaml.load(new FileInputStream(propertyFile));
        properties.putAll((Map<String, Object>) data);
        log.debug("File loaded");
    }

    /**
     * {@inheritDoc}
     */
    protected void storeConfiguration(final File file) throws IOException {
        final Yaml yaml = new Yaml();
        yaml.dump(properties, new FileWriter(file));
    }
}
