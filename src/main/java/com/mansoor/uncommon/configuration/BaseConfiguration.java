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
import com.mansoor.uncommon.configuration.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This abstract class provides a partial implementation of {@link Configuration} that is suitable for most implementation.
 * It is recommended to subclass this class instead of implementing {@link Configuration} directly
 *
 * @author Muhammad Ashraf
 * @since 2/25/12
 */
public abstract class BaseConfiguration extends Configuration {
    /**
     * Configuration registry used to retrieve property converters.
     */
    protected final ConverterRegistry converterRegistry;
    /**
     * Property deliminator character.
     */
    protected char deliminator = ',';
    /**
     * Lock that is used to block thread during destructive operations
     */
    protected final ReentrantLock lock = new ReentrantLock();
    /**
     * Configuration file being used.
     */
    private File config;
    /**
     * Scheduler used to schedule reload
     */
    protected final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);
    /**
     * Last modified time stamp of the configuration file.
     */
    protected Long lastModified;
    /**
     * Separator used to split nested keys.
     */
    public static final String NESTED_SEPARATOR = "(\\.)";
    private static final Logger log = LoggerFactory.getLogger(BaseConfiguration.class);

    /**
     * Creates an instances of using given {@code ConverterRegistry}
     *
     * @param converterRegistry converterRegistry that will be used by this instance.
     */
    protected BaseConfiguration(final ConverterRegistry converterRegistry) {
        Preconditions.checkNull(converterRegistry, "ConverterRegistry is null");
        this.converterRegistry = converterRegistry;
    }

    /**
     * Sets the deliminator character that will be used to split the values
     * <pre>
     *     configuration.setDeliminator('-');
     * </pre>
     *
     * @param deliminator deliminator
     */
    public void setDeliminator(final char deliminator) {
        this.deliminator = deliminator;
    }

    /**
     * Converts the input to String, associate it with the given key and sets it in the configuration.
     * <pre>
     *      propertyConfiguration.set("env","production");
     *      result: env = production
     *
     *      yamlConfiguration.set("env","production");
     *      result: env: production
     *
     *      jsonConfiguration.set("env","production");
     *      result: "env": "production"
     * </pre>
     *
     * @param key   the key for the value to set
     * @param input value to set
     * @since {@code 0.1}
     */
    @SuppressWarnings("unchecked")
    public <E> void set(final String key, final E input) {
        Preconditions.checkNull(input, "input is null");
        final Converter<E> converter = converterRegistry.getConverter((Class<E>) input.getClass());
        lock.lock();
        try {
            setProperty(key, converter.toString(input));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Converts the value associated with {@code key} to type {@code E} and returns it.
     * <p/>
     * <pre>
     *      Properties File
     *      expiration= 02/23/2012
     *      Date expiration = propertyConfiguration.get(Date.class, "expiration");
     *
     *      Yaml
     *      expiration: 02/23/2012
     *      Date expiration = yamlConfiguration.get(Date.class, "expiration");
     *
     *      Json
     *      "expiration": "02/23/2012"
     *      Date expiration = yamlConfiguration.get(Date.class, "expiration");
     *
     *      System variable
     *      java.io.tmpdir= /tmp
     *      File tmpDir = systemPropertyConfiguration.get(File.class, "java.io.tmpdir");
     *  </pre>
     *
     * @param type type the raw value will be converted to
     * @param key  key to use to retrieve the value
     * @return converted value
     * @since {@code 0.1}
     */
    public <E> E get(final Class<E> type, final String key) {
        final Converter<E> converter = converterRegistry.getConverter(type);
        try {
            return converter.convert(getProperty(key));
        } catch (Exception e) {
            throw new PropertyConversionException("conversion failed", e);
        }

    }

    /**
     * Converts the value associated with this nested key and convert it to type {@code E}.
     * <pre>
     *      Properties File
     *      development.password.database= secret_password
     *      String password = propertyConfiguration.getNested(String.class, "development.password.database");
     *
     *      Yaml
     *      development:
     *            password:
     *                database: secret_password
     *      String password = yamlConfiguration.getNested(String.class, "development.password.database");
     *
     *      Json
     *      "development":{
     *            "password":{
     *                "database": "secret_password"
     *                }
     *               }
     *      String password = jsonConfiguration.getNested(String.class, "development.password.database");
     *
     *      System variable
     *      java.io.tmpdir= /tmp
     *      File tmpDir = systemPropertyConfiguration.getNested(File.class, "java.io.tmpdir");
     *  </pre>
     *
     * @param type type the raw value will be converted to
     * @param key  nested key to use to retrieve the value
     * @return value of type {@code E}
     * @since {@code 0.1}
     */
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public void load(final String path) {
        Preconditions.checkNull(path, "path is null");
        final File file = new File(path);
        load(file);
    }

    /**
     * {@inheritDoc}
     */
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
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    public void clear() {
        lock.lock();
        try {
            clearConfig();
        } finally {
            lock.unlock();
        }

    }

    /**
     * {@inheritDoc}
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


    /**
     * Runnable used to poll configuration for changes.
     */
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

    /**
     * Store configuration to the given file
     *
     * @param file file where configuration will be saved.
     * @throws IOException if saving fails.
     */
    protected abstract void storeConfiguration(File file) throws IOException;

    /**
     * Sets the key and value in the configuration.
     *
     * @param key   key that will be use to set the property
     * @param value value to set
     */
    protected abstract void setProperty(final String key, Object value);

    /**
     * Returns the property mapped to the given key
     *
     * @param key key to retrieve the value
     * @return value mapped to the given key
     */
    protected abstract String getProperty(String key);

    /**
     * Loads the configuration in the given file.
     *
     * @param propertyFile configuration file
     * @throws IOException if loading fails
     */
    protected abstract void loadConfig(final File propertyFile) throws IOException;

    /**
     * Clears the configuration
     */
    protected abstract void clearConfig();

    /**
     * Returns the value using the nested key
     *
     * @param key nested key
     * @return vlaue mapped to nested key
     */
    protected abstract Object getNestedValue(final String key);
}
