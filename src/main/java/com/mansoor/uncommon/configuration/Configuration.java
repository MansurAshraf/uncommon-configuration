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
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public interface Configuration {
    <E> E get(Class<E> type, String key);

    <E> List<E> getList(Class<E> type, String key);

    <E> E getNested(Class<E> type, String key);

    <E> List<E> getNestedAsList(Class<E> type, String key);

    <E> void set(String key, E input);

    <E> void setList(String key, List<E> input);

    <E> void setList(final String key, final E... input);

    <E> void setNested(String key, E input);

    void load(File file);

    void load(String path);

    ConverterRegistry getConverterRegistry();

    void reload();

    void stopPolling();

    /**
     * Saves the configuration to the given path
     *
     * @param path path where the file will be saved
     * @return file where the config is saved
     */
    File save(String path);

    void clear();

}
