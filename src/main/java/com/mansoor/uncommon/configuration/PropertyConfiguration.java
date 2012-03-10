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

import com.mansoor.uncommon.configuration.Convertors.Converter;
import com.mansoor.uncommon.configuration.Convertors.ConverterRegistry;
import com.mansoor.uncommon.configuration.Convertors.DefaultConverterRegistry;
import com.mansoor.uncommon.configuration.functional.FunctionalCollection;
import com.mansoor.uncommon.configuration.functional.functions.IndexedBinaryFunction;
import com.mansoor.uncommon.configuration.transformers.PropertyTransformer;
import com.mansoor.uncommon.configuration.util.Preconditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

    /**
     * Returns an instance of {@code PropertyConfiguration} that is configured to use
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

    /**
     * Returns an instance of {@code PropertyConfiguration} that is configured to poll configuration file for change
     * @param converterRegistry registry that will be used by this PropertyConfiguration
     * @param pollingRate  polling rate
     * @param timeUnit time unit (eg: seconds, minute etc)
     */
    public PropertyConfiguration(final ConverterRegistry converterRegistry, final long pollingRate, final TimeUnit timeUnit) {
        super(converterRegistry);
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        properties = createProperties();
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }

    /**
     * Returns an instance of {@code PropertyConfiguration} that is configured to poll configuration file for change
     * @param pollingRate  polling rate
     * @param timeUnit time unit (eg: seconds, minute etc)
     */
    public PropertyConfiguration(final long pollingRate, final TimeUnit timeUnit) {
        super(new DefaultConverterRegistry());
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        properties = createProperties();
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }

    /**
     * Creates an instance of {@link Properties}
     * @return properties object
     */
    protected Properties createProperties() {
        return new Properties();
    }

    /**{@inheritDoc}*/
    public <E> List<E> getList(final Class<E> type, final String key) {
        final String property = getProperty(key);
        return splitAndConvert(type, property);
    }

    /**
     * Splits the given property using deliminator and converts all the value to type <code>E</code>
     * @param type type this property will be converted to
     * @param property property that will be split and converted
     * @param <E> generic type
     * @return List of E
     */
    protected <E> List<E> splitAndConvert(final Class<E> type, final String property) {
        List<E> result = null;
        if (Preconditions.isNotNull(property)) {
            result = new FunctionalCollection<String>(property.split(new String(new char[]{deliminator}))).map(new PropertyTransformer<E>(type, converterRegistry)).asList();
        }
        return result;
    }


    /**
     * Creates a String representation of the given list and associate it with the given key
     *
     * @param key    key
     * @param values list of value
     * @param <E>    Type of list
     */
    @SuppressWarnings(value = "unchecked")
    public <E> void setList(final String key, final List<E> values) {
        if (Preconditions.isNotEmpty(values)) {
            final Converter<E> converter = converterRegistry.getConverter((Class<E>) values.get(0).getClass());
            final StringBuilder stringBuilder = convertListToStringBuilder(values, converter);
            setProperty(key, stringBuilder.toString());
        }
    }

    /**
     * Concatenate all the values in the given list, using deliminator, and return them as <code>StringBuilder</code>
     * @param values List that will be converted to StringBuilder
     * @param converter converter that will convert the values.
     * @param <E> generic type E
     * @return StringBuilder containing all the values in the list
     */
    protected <E> StringBuilder convertListToStringBuilder(final List<E> values, final Converter<E> converter) {
        return new FunctionalCollection<E>(values).foldLeft(new StringBuilder(), new IndexedBinaryFunction<E, StringBuilder>() {
            public StringBuilder apply(final StringBuilder seed, final E input, final Integer index) {
                seed.append(converter.toString(input)).append(index < values.size() - 1 ? deliminator : "");
                return seed;
            }
        });
    }

    /**{@inheritDoc}*/
    public <E> List<E> getNestedList(final Class<E> type, final String key) {
        final Object nestedValue = getNestedValue(key);
        return splitAndConvert(type, nestedValue != null ? nestedValue.toString() : null);

    }

    /**
     * Creates a String representation of the given list and associate it with the given key
     *
     * @param key   key
     * @param input list of value
     * @param <E>   Type of list
     */
    public <E> void setList(final String key, final E... input) {
        if (Preconditions.isNotNull(input)) {
            setList(key, Arrays.asList(input));
        }
    }

    protected String getProperty(final String key) {
        return properties.getProperty(key);
    }

    protected void setProperty(final String key, final Object value) {
        properties.setProperty(key, value.toString());
    }


    protected void loadConfig(final File propertyFile) throws IOException {
        properties.load(new FileInputStream(propertyFile));
    }

    protected void clearConfig() {
        properties.clear();
    }

    protected Object getNestedValue(final String key) {
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

    public <E> void setNested(final String key, final E input) {
        set(key, input);
    }

    public <E> void setNestedList(final String key, final List<E> input) {
        setList(key, input);
    }

    public <E> void setNestedList(final String key, final E... input) {
        setList(key, input);
    }

}
