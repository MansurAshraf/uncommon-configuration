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
import com.mansoor.uncommon.configuration.exceptions.PropertyConversionException;
import com.mansoor.uncommon.configuration.functional.FunctionalCollection;
import com.mansoor.uncommon.configuration.functional.functions.BinaryFunction;
import com.mansoor.uncommon.configuration.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Muhammad Ashraf
 * @since 2/25/12
 */
public abstract class BaseConfiguration implements Configuration {
    protected final ConverterRegistry converterRegistry;
    protected char deliminator = ',';
    protected final ReentrantLock lock = new ReentrantLock();
    protected File config;
    protected final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);
    protected Long lastModified;
    public static final String NESTED_SEPARATOR = "(\\.)";
    private static final Logger log = LoggerFactory.getLogger(BaseConfiguration.class);

    protected BaseConfiguration(final ConverterRegistry converterRegistry) {
        Preconditions.checkNull(converterRegistry, "ConverterRegistry is null");
        this.converterRegistry = converterRegistry;
    }

    /**
     * Sets the deliminator character that will be used to split the values
     *
     * @param deliminator deliminator
     */
    public void setDeliminator(final char deliminator) {
        this.deliminator = deliminator;
    }

    @SuppressWarnings("unchecked")
    public <E> void set(final String key, final E input) {
        if (Preconditions.isNotNull(input)) {
            final Converter<E> converter = converterRegistry.getConverter((Class<E>) input.getClass());
            lock.lock();
            try {
                setProperty(key, converter.toString(input));
            } finally {
                lock.unlock();
            }
        }
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
        try {
            return converter.convert(getProperty(key));
        } catch (Exception e) {
            throw new PropertyConversionException("conversion failed", e);
        }

    }

    public <E> E getNested(final Class<E> type, final String key) {

        final Object value = getNestedValue(key);
        E result = null;
        if (Preconditions.isNotNull(value)) {
            final Converter<E> converter = converterRegistry.getConverter(type);
            try {
                result = converter.convert(String.class.isAssignableFrom(value.getClass()) ? (String) value : value.toString());
            } catch (Exception e) {
                throw new PropertyConversionException("conversion failed", e);
            }
        }
        return result;
    }

    /**
     * Loads the given property file
     *
     * @param propertyFile property file
     */
    public void load(final File propertyFile) {
        Preconditions.checkNull(propertyFile, "File is null");
        this.config = propertyFile;
        lastModified = propertyFile.lastModified();

        lock.lock();
        try {
            loadConfig(propertyFile);
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
        Preconditions.checkNull(path, "path is null");
        final URL resource = BaseConfiguration.class.getResource(path);
        Preconditions.checkNull(resource, "unable to load file with path " + path);
        final File file = new File(resource.getPath());
        load(file);
    }

    public void reload() {
        lock.lock();
        try {
            if (config != null) {
                log.info("Reloading properties file " + config.getAbsolutePath());
                clearConfig();
                loadConfig(config);
                log.info("Reloading done");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to reload file " + config, e);
        } finally {
            lock.unlock();
        }

    }


    /**
     * Returns the underlying Converter Registry
     *
     * @return ConverterRegistry
     */
    public ConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    /**
     * Stops file polling
     */
    public void stopPolling() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    public void clear() {
        lock.lock();
        try {
            clearConfig();
        } finally {
            lock.unlock();
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
            storeConfiguration(file);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save file", e);
        } finally {
            lock.unlock();
        }
        return file;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getInnerMap(final Map<String, Object> map, final List<String> keys) {
        return new FunctionalCollection<String>(keys.subList(0, keys.size() - 1)).foldLeft(map, new BinaryFunction<String, Map<String, Object>>() {
            public Map<String, Object> apply(final Map<String, Object> seed, final String input) {
                final Map<String, Object> result;
                if (seed.containsKey(input)) {
                    result = (Map<String, Object>) seed.get(input);
                } else {
                    result = new HashMap<String, Object>();
                    lock.lock();
                    try {
                        seed.put(input, result);
                    } finally {
                        lock.unlock();
                    }
                }
                return result;
            }
        });
    }


    protected abstract void storeConfiguration(File file) throws IOException;

    protected abstract void setProperty(final String key, String s);

    protected abstract String getProperty(String key);

    protected abstract void loadConfig(final File propertyFile) throws IOException;

    protected abstract void clearConfig();

    protected abstract Object getNestedValue(final String key);
}
