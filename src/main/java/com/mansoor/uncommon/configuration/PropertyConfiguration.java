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
import com.mansoor.uncommon.configuration.util.Preconditions;
import com.mansoor.uncommon.configuration.util.Throwables;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author mansoor
 * @since 2/9/12
 */
public class PropertyConfiguration implements Configuration {
    private final ConverterRegistry converterRegistry;
    private final Properties properties = new Properties();

    public PropertyConfiguration() {
        this.converterRegistry = new DefaultConverterRegistry();
    }

    public PropertyConfiguration(final ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public <E> E get(Class<E> type, String key) {
        final Converter<E> converter = converterRegistry.getConverter(type);
        return getAndConvert(converter, key);
    }

    private <E> E getAndConvert(Converter<E> converter, String key) {
        try {
            return converter.convert(properties.getProperty(key));
        } catch (Exception e) {
            throw Throwables.propertyConversionException("conversion failed", e);
        }

    }

    public void load(final File propertyFile) {
        try {
            Preconditions.checkNull(propertyFile, "File is null");
            properties.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load file " + propertyFile, e);

        }
    }

    public void load(final InputStream inputStream) {
        try {
            Preconditions.checkNull(inputStream, "InputStream is null");
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
