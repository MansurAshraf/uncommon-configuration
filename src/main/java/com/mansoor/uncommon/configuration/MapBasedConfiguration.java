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
import com.mansoor.uncommon.configuration.functional.FunctionalCollection;
import com.mansoor.uncommon.configuration.functional.functions.BinaryFunction;
import com.mansoor.uncommon.configuration.functional.functions.UnaryFunction;
import com.mansoor.uncommon.configuration.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Muhammad Ashraf
 * @since 3/9/12
 */
public abstract class MapBasedConfiguration extends BaseConfiguration {
    protected final Map<String, Object> properties;
    private final static Logger log = LoggerFactory.getLogger(MapBasedConfiguration.class);

    protected MapBasedConfiguration(final ConverterRegistry converterRegistry, final Map<String, Object> properties) {
        super(converterRegistry);
        this.properties = properties;
    }

    protected void setProperty(final String key, final Object value) {
        log.debug("Storing Key ['()'] with value ['()']", key, value);
        properties.put(key, value);
    }


    protected String getProperty(final String key) {
        final Object value = properties.get(key);
        log.debug("Returning Key ['()'] with value ['()']", key, value);
        return convertValueToString(value);
    }


    protected void clearConfig() {
        log.debug("clearing config");
        properties.clear();
    }

    protected Object getNestedValue(final String key) {
        Preconditions.checkBlank(key, "Key is null or blank");
        final List<String> keys = Arrays.asList(key.split(NESTED_SEPARATOR));

        return new FunctionalCollection<String>(keys).foldLeft(properties, new BinaryFunction<String, Object>() {
            public Object apply(final Object seed, final String input) {
                Object result = seed;
                if (Preconditions.isNotNull(seed) && seed instanceof HashMap) {
                    result = ((HashMap) seed).get(input);
                }
                return result;
            }
        });
    }

    public <E> List<E> getList(final Class<E> type, final String key) {
        final Object value = properties.get(key);
        return transformList(type, value);
    }


    public <E> List<E> getNestedList(final Class<E> type, final String key) {
        final Object nestedValue = getNestedValue(key);
        return transformList(type, nestedValue);
    }

    public <E> void setList(final String key, final List<E> input) {
        Preconditions.checkArgument(Preconditions.isNotNull(key), "key is null");
        Preconditions.checkArgument(Preconditions.isNotEmpty(input), "input is empty");
        final List<String> result = transformList(input);
        lock.lock();
        try {
            setProperty(key, result);
        } finally {
            lock.unlock();
        }

    }

    @SuppressWarnings("unchecked")
    private <E> List<E> transformList(final Class<E> type, final Object value) {
        List<E> result = null;
        if (Preconditions.isNotNull(value)) {
            Preconditions.checkArgument(List.class.isAssignableFrom(value.getClass()), "Expecting a List but found " + value);
            final List<String> unconvertedList = (List<String>) value;
            final Converter<E> converter = converterRegistry.getConverter(type);
            result = new FunctionalCollection<String>(unconvertedList).map(new UnaryFunction<String, E>() {
                public E apply(final String input) {
                    return converter.convert(input);
                }
            }).asList();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <E> List<String> transformList(final List<E> input) {
        final Converter<E> converter = (Converter<E>) converterRegistry.getConverter(input.get(0).getClass());
        return new FunctionalCollection<E>(input).map(new UnaryFunction<E, String>() {
            public String apply(final E input) {
                return converter.toString(input);
            }
        }).asList();
    }

    public <E> void setList(final String key, final E... input) {
        setList(key, Arrays.asList(input));
    }

    @SuppressWarnings("unchecked")
    public <E> void setNested(final String key, final E input) {
        Preconditions.checkNull(input, "input is null");
        final Converter<E> converter = (Converter<E>) converterRegistry.getConverter(input.getClass());
        final String value = converter.toString(input);
        setNestedStringValue(key, value);

    }


    @SuppressWarnings("unchecked")
    public <E> void setNestedList(final String key, final List<E> input) {
        Preconditions.checkNull(key, "key is null");
        Preconditions.checkArgument(Preconditions.isNotEmpty(input), "List is null or empty");
        final List<String> keys = Arrays.asList(key.split(NESTED_SEPARATOR));
        final List<String> value = transformList(input);
        if (keys.size() == 1) {
            setProperty(keys.get(0), value);
        } else {
            final Map<String, Object> innerMap = getInnerMap(properties, keys);
            lock.lock();
            try {
                innerMap.put(keys.get(keys.size() - 1), value);
            } finally {
                lock.unlock();
            }
        }


    }

    public <E> void setNestedList(final String key, final E... input) {
        Preconditions.checkArgument(input != null && input.length > 0, "input is empty");
        setNestedList(key, Arrays.asList(input));
    }

    @SuppressWarnings("unchecked")
    private void setNestedStringValue(final String key, final String value) {
        Preconditions.checkBlank(key, "Key is null or blank");
        final List<String> keys = Arrays.asList(key.split(NESTED_SEPARATOR));
        if (keys.size() == 1) {
            lock.lock();
            try {
                setProperty(key, value);
            } finally {
                lock.unlock();
            }
        } else {
            final Map<String, Object> map = getInnerMap(properties, keys);
            lock.lock();
            try {
                map.put(keys.get(keys.size() - 1), value);
            } finally {
                lock.unlock();
            }
        }
    }


    protected String convertValueToString(final Object value) {
        String result = null;
        if (Preconditions.isNotNull(value)) {
            if (String.class.isAssignableFrom(value.getClass())) {
                result = (String) value;
            } else {
                result = value.toString();
            }
        }
        return result;
    }

}
