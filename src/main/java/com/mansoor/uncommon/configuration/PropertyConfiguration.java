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
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author mansoor
 * @since 2/9/12
 */
public class PropertyConfiguration implements Configuration {
    private final ConverterRegistry converterRegistry;
    private final Properties properties;
    private char deliminator = ',';

    public PropertyConfiguration() {
        this.converterRegistry = new DefaultConverterRegistry();
        properties = loadPropertiesFile();
    }

    public PropertyConfiguration(final ConverterRegistry converterRegistry) {
        Preconditions.checkNull(converterRegistry, "ConverterRegistry is null");
        this.converterRegistry = converterRegistry;
        properties = loadPropertiesFile();
    }

    public <E> E get(final Class<E> type, final String key) {
        final Converter<E> converter = converterRegistry.getConverter(type);
        return getAndConvert(converter, key);
    }

    public <E> List<E> getList(final Class<E> type, final String key) {
        final String property = properties.getProperty(key);
        List<E> result = null;
        if (Preconditions.isNotNull(property)) {
            result = new FunctionalCollection<String>(property.split(new String(new char[]{deliminator}))).map(new PropertyTransformer<E>(type)).asList();
        }

        return result;
    }

    public <E> void set(final Class<E> type, final String key, final E input) {
        final Converter<E> converter = converterRegistry.getConverter(type);
        properties.setProperty(key, converter.toString(input));
    }

    public <E> void setList(final Class<E> type, final String key, final List<E> input) {
        if (Preconditions.isNotEmpty(input)) {
            final Converter<E> converter = converterRegistry.getConverter(type);
            final StringBuffer stringBuffer = new FunctionalCollection<E>(input).foldLeft(new StringBuffer(), new BinaryFunction<E, StringBuffer>() {
                public StringBuffer apply(final StringBuffer seed, final E input) {
                    seed.append(converter.toString(input)).append(deliminator);
                    return seed;
                }
            });
            final String value = stringBuffer.substring(stringBuffer.length() - 1);
            properties.setProperty(key, value);
        }
    }

    public <E> void setList(final Class<E> type, final String key, final E... input) {
        if (Preconditions.isNotNull(input)) {
            setList(type, key, Arrays.asList(input));
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


    public ConverterRegistry getConverterRegistry() {
        return converterRegistry;
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
