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
import com.mansoor.uncommon.configuration.exceptions.PropertyConversionException;
import com.mansoor.uncommon.configuration.functional.FunctionalCollection;
import com.mansoor.uncommon.configuration.functional.functions.BinaryFunction;
import com.mansoor.uncommon.configuration.functional.functions.UnaryFunction;
import com.mansoor.uncommon.configuration.util.Preconditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This Class is a type safe wrapper over {@link Properties} class and provides convenient methods to easily
 * manipulate Properties file
 *
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public class PropertyConfiguration implements Configuration {
    private final ConverterRegistry converterRegistry;
    private final Properties properties;
    private char deliminator = ',';
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private File propertyFile;


    /**
     * Returns an instance of {@code PropertyConfiguration} that is configured to used
     * {@link DefaultConverterRegistry}
     */
    public PropertyConfiguration() {
        this.converterRegistry = new DefaultConverterRegistry();
        properties = loadPropertiesFile();
    }

    /**
     * Returns an instance of {@code PropertyConfiguration} configured with given Converter Registry
     *
     * @param converterRegistry registry that will be used by this PropertyConfiguration
     */
    public PropertyConfiguration(final ConverterRegistry converterRegistry) {
        Preconditions.checkNull(converterRegistry, "ConverterRegistry is null");
        this.converterRegistry = converterRegistry;
        properties = loadPropertiesFile();
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param type Type that the value will be converted too
     * @param key  key that will be used to retrieve the value
     * @param <E>  Type parameter
     * @return value of type E
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *          if no converter is configured for the given type
     */
    public <E> E get(final Class<E> type, final String key) {
        final Converter<E> converter = converterRegistry.getConverter(type);
        return getAndConvert(converter, key);
    }

    /**
     * Retrieves the value associated with the given key and break it into list by using a predefined delimiter.
     *
     * @param type Type that the value will be converted to
     * @param key  key the value is associated with
     * @param <E>  Type parameter
     * @return List of type E
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *          if no converter is configured for the given type
     */
    public <E> List<E> getList(final Class<E> type, final String key) {
        final String property = properties.getProperty(key);
        List<E> result = null;
        if (Preconditions.isNotNull(property)) {
            result = new FunctionalCollection<String>(property.split(new String(new char[]{deliminator}))).map(new PropertyTransformer<E>(type)).asList();
        }

        return result;
    }

    /**
     * Sets the key and value
     *
     * @param key   key
     * @param input value
     * @param <E>   type of value
     */
    @SuppressWarnings(value = "unchecked")
    public <E> void set(final String key, final E input) {
        if (Preconditions.isNotNull(input)) {
            final Converter<E> converter = converterRegistry.getConverter((Class<E>) input.getClass());
            properties.setProperty(key, converter.toString(input));
        }
    }

    /**
     * Creates a String representation of the given list and associate it with the given key
     *
     * @param key   key
     * @param input list of value
     * @param <E>   Type of list
     */
    @SuppressWarnings(value = "unchecked")
    public <E> void setList(final String key, final List<E> input) {
        if (Preconditions.isNotEmpty(input)) {
            final Converter<E> converter = converterRegistry.getConverter((Class<E>) input.get(0).getClass());
            final StringBuffer stringBuffer = new FunctionalCollection<E>(input).foldLeft(new StringBuffer(), new BinaryFunction<E, StringBuffer>() {
                public StringBuffer apply(final StringBuffer seed, final E input) {
                    seed.append(converter.toString(input)).append(deliminator);
                    return seed;
                }
            });
            final String value = stringBuffer.substring(0, stringBuffer.lastIndexOf(String.valueOf(deliminator)));
            properties.setProperty(key, value);
        }
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

    /**
     * Loads the given property file
     *
     * @param propertyFile property file
     */
    public void load(final File propertyFile) {
        this.propertyFile = propertyFile;
        try {
            Preconditions.checkNull(propertyFile, "File is null");
            properties.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load file " + propertyFile, e);

        }
    }

    /**
     * Loads the property file associated with the given input stream
     *
     * @param path file path
     */
    public void load(final String path) {
        Preconditions.checkNull(path, "InputStream is null");
        final File file = new File(this.getClass().getResource(path).getPath());
        load(file);
    }

    /**
     * Returns the underlying Properties instance
     *
     * @return Properties
     */
    public Properties toProperties() {
        return properties;
    }

    /**
     * Returns the underlying Converter Registry
     *
     * @return ConverterRegistry
     */
    public ConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    public void reload() {
        writeLock.lock();
        readLock.lock();
        try {
            properties.clear();
            properties.load(new FileInputStream(propertyFile));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to reload file " + propertyFile, e);
        } finally {
            readLock.unlock();
            writeLock.unlock();
        }

    }

    private <E> E getAndConvert(final Converter<E> converter, final String key) {
        try {
            return converter.convert(properties.getProperty(key));
        } catch (Exception e) {
            throw new PropertyConversionException("conversion failed", e);
        }

    }

    Properties loadPropertiesFile() {
        return new Properties();
    }

    /**
     * Sets the deliminator character that will be used to split the values
     *
     * @param deliminator deliminator
     */
    public void setDeliminator(final char deliminator) {
        this.deliminator = deliminator;
    }

    private class PropertyTransformer<E> implements UnaryFunction<String, E> {
        private final Class<E> type;

        public PropertyTransformer(final Class<E> type) {
            this.type = type;
        }

        public E apply(final String input) {
            final Converter<E> converter = converterRegistry.getConverter(type);
            return converter.convert(input);
        }
    }
}
