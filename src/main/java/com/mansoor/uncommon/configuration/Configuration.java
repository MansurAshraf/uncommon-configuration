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
import java.util.List;

/**
 * The main configuration interface.
 * <p>
 * This interface contain various type safe methods to manipulate underlying configuration.
 * </p>
 * <p>
 * Property retrieval methods such as {@code get},{@code getList} and {@code getNestedMethods} return
 * {@code null} if property is not found. Unrecoverable errors are converted to {@code unchecked exceptions} and
 * thrown to the caller.
 * </p>
 * <p>
 * Setter methods do a strict validation and throw {@link IllegalArgumentException} if either {@code Key} or
 * {@code value} are null. In the case of {@code setList} and {@code setNestedList} method an empty List also
 * results in {@link IllegalArgumentException}.
 * <p/>
 * <p>
 * It is recommended that instead of implementing this interface directly, {@link BaseConfiguration} class should be
 * overriden as it already provides an implementation for most methods.
 * </p>
 *
 * @author Muhammad Ashraf
 * @since 0.1
 */
public abstract class Configuration {
    /**
     * Converts the value associated with {@code key} to type {@code E} and returns it. This method is
     * thread safe and non blocking
     *
     * @param type type the raw value will be converted to
     * @param key  key to use to retrieve the value
     * @param <E>  generic type E
     * @return converted value
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *          if no converter is
     *          configured to convert the value to type {@code E} and  com.mansoor.uncommon.configuration.exceptions.PropertyConversionException
     *          if conversion fails.
     * @since {@code 0.1}
     */
    public abstract <E> E get(Class<E> type, String key);

    /**
     * Converts all the values associated with this key and convert them to type {@code E}.This method is
     * thread safe and non blocking.
     *
     * @param type type the raw value will be converted to
     * @param key  key to use to retrieve the value
     * @param <E>  generic type E
     * @return List of type {@code E}
     * @since {@code 0.1}
     */
    public abstract <E> List<E> getList(Class<E> type, String key);

    /**
     * Converts the value associated with this nested key and convert it to type {@code E}.This method is
     * thread safe and non blocking.
     *
     * @param type type the raw value will be converted to
     * @param key  nested key to use to retrieve the value
     * @param <E>  generic type E
     * @return value of type {@code E}
     * @since {@code 0.1}
     */
    public abstract <E> E getNested(Class<E> type, String key);

    /**
     * Converts all the values associated with this nested key and convert them to type {@code E}.This method is
     * thread safe and non blocking.
     *
     * @param type type the raw value will be converted to
     * @param key  nested key to use to retrieve the value
     * @param <E>  generic type E
     * @return List of type {@code E}
     * @since {@code 0.1}
     */
    public abstract <E> List<E> getNestedList(Class<E> type, String key);

    /**
     * Converts the input to String, associate it with the given key and sets it in the configuration. This methods is
     * thread safe and blocking.
     *
     * @param key   the key for the value to set
     * @param input value to set
     * @param <E>   type of value
     * @throws IllegalArgumentException if key or value is null
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *                                  if no converter is
     *                                  configured to convert the value to {@code String}
     * @throws com.mansoor.uncommon.configuration.exceptions.PropertyConversionException
     *                                  if conversion fails.
     * @since {@code 0.1}
     */
    public abstract <E> void set(String key, E input);

    /**
     * Converts all the values in the input List to String, associate it with the given key and sets it in the configuration.
     * This methods is thread safe and blocking.
     *
     * @param key   the key for the value to set
     * @param input List of value to set
     * @param <E>   type of value
     * @throws IllegalArgumentException if key or value is null
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *                                  if no converter is
     *                                  configured to convert the value to {@code String}
     * @throws com.mansoor.uncommon.configuration.exceptions.PropertyConversionException
     *                                  if conversion fails.
     * @since {@code 0.1}
     */
    public abstract <E> void setList(String key, List<E> input);

    /**
     * Converts all the values in the input List to String, associate it with the given key and sets it in the configuration.
     * This methods is thread safe and blocking.
     *
     * @param key   the key for the value to set
     * @param input List of value to set
     * @param <E>   type of value
     * @throws IllegalArgumentException if key or value is null
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *                                  if no converter is
     *                                  configured to convert the value to {@code String}.
     * @throws com.mansoor.uncommon.configuration.exceptions.PropertyConversionException
     *                                  if conversion fails.
     * @since {@code 0.1}
     */
    public abstract <E> void setList(final String key, final E... input);

    /**
     * Converts the input to String, associate it with the given nested key and sets it in the configuration. This methods is
     * thread safe and blocking.
     *
     * @param key   the key for the value to set
     * @param input value to set
     * @param <E>   type of value
     * @throws IllegalArgumentException if key or value is null
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *                                  if no converter is configured to convert the value to {@code String}
     * @throws com.mansoor.uncommon.configuration.exceptions.PropertyConversionException
     *                                  if conversion fails.
     * @since {@code 0.1}
     */
    public abstract <E> void setNested(String key, E input);

    /**
     * Converts all the values in input {@code List} to String, associate it with the given nested key and sets it in the configuration. This methods is
     * thread safe and blocking.
     *
     * @param key   the key for the value to set
     * @param input List of value to set
     * @param <E>   type of value
     * @throws IllegalArgumentException if key or value is null
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *                                  if no converter is configured to convert the value to {@code String}
     * @throws com.mansoor.uncommon.configuration.exceptions.PropertyConversionException
     *                                  if conversion fails.
     * @since {@code 0.1}
     */
    public abstract <E> void setNestedList(String key, List<E> input);

    /**
     * Converts all the values in input {@code List} to String, associate it with the given nested key and sets it in the configuration. This methods is
     * thread safe and blocking.
     *
     * @param key   the key for the value to set
     * @param input List of value to set
     * @param <E>   type of value
     * @throws IllegalArgumentException if key or value is null
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException
     *                                  if no converter is configured to convert the value to {@code String}
     * @throws com.mansoor.uncommon.configuration.exceptions.PropertyConversionException
     *                                  if conversion fails.
     * @since {@code 0.1}
     */
    public abstract <E> void setNestedList(final String key, final E... input);

    /**
     * Loads the configuration from the given file
     *
     * @param file configuration file.
     */
    public abstract void load(File file);

    /**
     * Loads the configuration from the given file
     *
     * @param path configuration file path.
     */
    public abstract void load(String path);

    /**
     * Returns the {@link ConverterRegistry} used by this configuration.
     *
     * @return underlying configuration registry
     */
    public abstract ConverterRegistry getConverterRegistry();

    /**
     * Reloads the configuration file. All values that are not persisted are lost after reload.
     */
    public abstract void reload();

    /**
     * Saves the configuration to the given path
     *
     * @param path path where the file will be saved
     * @return file where the config is saved
     */
    public abstract File save(String path);

    /**
     * Clears all the value from the configuration.
     */
    public abstract void clear();

}
