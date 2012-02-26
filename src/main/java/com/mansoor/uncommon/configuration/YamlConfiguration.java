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
import com.mansoor.uncommon.configuration.util.Preconditions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Muhammad Ashraf
 * @since 2/25/12
 */
public class YamlConfiguration implements Configuration {
    public <E> E get(final Class<E> type, final String key) {
        return null;
    }

    public <E> List<E> getList(final Class<E> type, final String key) {
        return null;
    }

    public <E> void set(final String key, final E input) {

    }

    public <E> void setList(final String key, final List<E> input) {

    }

    public <E> void setList(final String key, final E... input) {

    }

    public void load(final File file) {
        Preconditions.checkNull(file, "File is null");
        final Yaml yaml = new Yaml();
        try {
            final Object load = yaml.load(new FileInputStream(file));
            System.out.println("load = " + load);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public void load(final String path) {

    }

    public ConverterRegistry getConverterRegistry() {
        return null;
    }

    public void reload() {

    }

    public void stopPolling() {

    }

    /**
     * Saves the configuration to the given path
     *
     * @param path path where the file will be saved
     * @return file where the config is saved
     */
    public File save(final String path) {
        return null;
    }

    public void clear() {

    }
}
