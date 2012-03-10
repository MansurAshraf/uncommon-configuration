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
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Muhammad Ashraf
 * @since 3/4/12
 */
public class JsonConfiguration extends MapBasedConfiguration {
    private static final Logger log = LoggerFactory.getLogger(JsonConfiguration.class);

    public JsonConfiguration(final ConverterRegistry converterRegistry) {
        super(converterRegistry, new HashMap<String, Object>());
    }

    public JsonConfiguration() {
        super(new DefaultConverterRegistry(), new HashMap<String, Object>());
    }


    public JsonConfiguration(final ConverterRegistry converterRegistry, final long pollingRate, final TimeUnit timeUnit) {
        super(converterRegistry, new HashMap<String, Object>());
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);

    }

    public JsonConfiguration(final long pollingRate, final TimeUnit timeUnit) {
        super(new DefaultConverterRegistry(), new HashMap<String, Object>());
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }


    @SuppressWarnings("unchecked")
    protected void storeConfiguration(final File file) throws IOException {
        final String value = JSONValue.toJSONString(properties);
        saveFile(file, value);
    }


    @SuppressWarnings("unchecked")
    protected void loadConfig(final File propertyFile) throws IOException {
        final Map<String, Object> map = (Map<String, Object>) JSONValue.parse(new FileReader(propertyFile));
        properties.putAll(map);
    }


    private void saveFile(final File file, final String json) {
        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(json);
            out.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to save file", e);
        }
    }

}


