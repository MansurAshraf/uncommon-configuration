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
import com.mansoor.uncommon.configuration.functional.functions.IndexedBinaryFunction;
import com.mansoor.uncommon.configuration.functional.functions.UnaryFunction;
import com.mansoor.uncommon.configuration.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This Class is a type safe wrapper over {@link Properties} class and provides convenient methods to easily
 * manipulate Properties file
 *
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public class PropertyConfiguration implements Configuration {
    private final ConverterRegistry converterRegistry;
    protected final Properties properties;
    private char deliminator = ',';
    protected final ReentrantLock lock = new ReentrantLock();
    private File propertyFile;
    private final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);
    private Long lastModified;
    private static final Logger log = LoggerFactory.getLogger(PropertyConfiguration.class);

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

    public PropertyConfiguration(final ConverterRegistry converterRegistry, final long pollingRate, final TimeUnit timeUnit) {
        Preconditions.checkNull(converterRegistry, "ConverterRegistry is null");
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        this.converterRegistry = converterRegistry;
        properties = loadPropertiesFile();
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
    }

    public PropertyConfiguration(final long pollingRate, final TimeUnit timeUnit) {
        Preconditions.checkArgument(pollingRate > 0, "Polling rate must be greater than 0");
        Preconditions.checkNull(timeUnit, "No Time Unit Specified");
        this.converterRegistry = new DefaultConverterRegistry();
        properties = loadPropertiesFile();
        executorService.scheduleAtFixedRate(new FilePoller(), pollingRate, pollingRate, timeUnit);
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
            setProperty(key, converter.toString(input));
        }
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
            final StringBuffer stringBuffer = new FunctionalCollection<E>(values).foldLeft(new StringBuffer(), new IndexedBinaryFunction<E, StringBuffer>() {
                public StringBuffer apply(final StringBuffer seed, final E input, final Integer index) {
                    seed.append(converter.toString(input)).append(index < values.size() - 1 ? deliminator : "");
                    return seed;
                }
            });
            setProperty(key, stringBuffer.toString());
        }
    }

    private void setProperty(final String key, final String value) {
        lock.lock();
        try {
            properties.setProperty(key, value);
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            Preconditions.checkNull(propertyFile, "File is null");
            this.propertyFile = propertyFile;
            properties.load(new FileInputStream(propertyFile));
            lastModified = propertyFile.lastModified();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load file " + propertyFile, e);

        } finally {
            lock.unlock();
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
     * Returns the underlying Converter Registry
     *
     * @return ConverterRegistry
     */
    public ConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    public void reload() {
        lock.lock();
        try {
            if (propertyFile != null) {
                log.info("Reloading properties file " + propertyFile.getAbsolutePath());
                properties.clear();
                properties.load(new FileInputStream(propertyFile));
                log.info("Reloading done");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to reload file " + propertyFile, e);
        } finally {
            lock.unlock();
        }

    }

    /**
     * Stops file polling
     */
    public void stopPolling() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Saves the configuration to the given path
     *
     * @param path path where the file will be saved
     * @return file where the config is saved
     */
    public File save(final String path) {
        Preconditions.checkBlank(path, "path is null or empty");
        final File file = new File(path);
        lock.lock();
        try {
            properties.store(new FileOutputStream(file), "");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save file", e);
        } finally {
            lock.unlock();
        }
        return file;
    }

    public void clear() {
        lock.lock();
        try {
            properties.clear();
        } finally {
            lock.unlock();
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

    /**
     * Transformer class used to transform a STring to a given type
     *
     * @param <E>
     */
    class PropertyTransformer<E> implements UnaryFunction<String, E> {
        private final Class<E> type;

        public PropertyTransformer(final Class<E> type) {
            this.type = type;
        }

        public E apply(final String input) {
            final Converter<E> converter = converterRegistry.getConverter(type);
            return converter.convert(input);
        }
    }


    class FilePoller implements Runnable {
        public void run() {
            log.info("Polling File");
            final File temp = new File(propertyFile.getAbsolutePath());
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
