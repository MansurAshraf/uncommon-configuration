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
 * Abstract class containing methods used by map based configurations such as {@link YamlConfiguration} and {@link JsonConfiguration}
 * @author Muhammad Ashraf
 * @since 3/9/12
 */
public abstract class MapBasedConfiguration extends BaseConfiguration {
    /**
     * Properties map.
     */
    protected final Map<String, Object> properties;
    private final static Logger log = LoggerFactory.getLogger(MapBasedConfiguration.class);

    /**
     * Creates an instance using given converterRegistry and properties map
     * @param converterRegistry instance of converterRegistry that this instance will use.
     * @param properties  properties map.
     */
    protected MapBasedConfiguration(final ConverterRegistry converterRegistry, final Map<String, Object> properties) {
        super(converterRegistry);
        this.properties = properties;
    }

    /**{@inheritDoc}*/
    protected void setProperty(final String key, final Object value) {
        log.debug("Storing Key ['()'] with value ['()']", key, value);
        properties.put(key, value);
    }

    /**{@inheritDoc}*/
    protected String getProperty(final String key) {
        final Object value = properties.get(key);
        log.debug("Returning Key ['()'] with value ['()']", key, value);
        return convertValueToString(value);
    }

    /**{@inheritDoc}*/
    protected void clearConfig() {
        log.debug("clearing config");
        properties.clear();
    }

    /**{@inheritDoc}*/
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

    /**
     * Retrieves the value of the given key and converts it into a list of
     * of given type
     *   <pre>
     *      Properties File
     *      files= /tmp/doc1.txt,/tmp/doc2.txt
     *      List<File> files = propertyConfiguration.getList(File.class, "files");
     *
     *      Yaml
     *      files: [/tmp/doc1.txt,/tmp/doc2.txt]
     *      List<File> files = yamlConfiguration.getList(File.class, "files");
     *
     *      Json
     *      "files": [/tmp/doc1.txt,/tmp/doc2.txt]
     *      List<File> files = jsonConfiguration.getList(File.class, "files");
     *
     *      System variable
     *      List<File> classpath  = systemPropertyConfiguration.getList(File.class, "java.class.path");
     *  </pre>
     *
     * @param type type the raw value will be converted to
     * @param key  key to use to retrieve the value
     * @return List of type {@code E}
     * @since {@code 0.1}
     */
    public <E> List<E> getList(final Class<E> type, final String key) {
        final Object value = properties.get(key);
        return transformList(type, value);
    }

    /**
     * Converts all the values associated with this nested key, converts them to List of given type
     * and returns the list
     *   <pre>
     *      Properties File
     *      temporary.files= /tmp/doc1.txt,/tmp/doc2.txt
     *      List<File> files = propertyConfiguration.getNestedList(File.class, "temporary.files");
     *
     *      Yaml
     *      temporary:
     *          files: [/tmp/doc1.txt,/tmp/doc2.txt]
     *      List<File> files = yamlConfiguration.getNestedList(File.class, "temporary.files");
     *
     *      Json
     *      "temporary":{
     *          "files": [/tmp/doc1.txt,/tmp/doc2.txt]
     *          }
     *      List<File> files = jsonConfiguration.getNestedList(File.class, "temporary.files");
     *
     *      System variable
     *      List<File> classpath  = systemPropertyConfiguration.getNestedList(File.class, "java.class.path");
     *  </pre>
     *
     * @param type type the raw value will be converted to
     * @param key  nested key to use to retrieve the value
     * @return List of type {@code E}
     * @since {@code 0.1}
     */
    public <E> List<E> getNestedList(final Class<E> type, final String key) {
        final Object nestedValue = getNestedValue(key);
        return transformList(type, nestedValue);
    }

    /**
     * Converts all the values in the input List to String, associate it with the given key
     * and sets it in the configuration.
     *
     *  <pre>
     *      Properties File
     *      List<File> files = Arrays.asList(new File(/tmp/doc1.txt),new File(/tmp/doc2.txt));
     *      propertyConfiguration.setList("tempFiles",files);
     *      tempFiles= /tmp/doc1.txt,/tmp/doc2.txt
     *
     *      Yaml
     *      List<File> files = Arrays.asList(new File(/tmp/doc1.txt),new File(/tmp/doc2.txt));
     *      yamlConfiguration.setList("tempFiles",files);
     *      tempFiles: [/tmp/doc1.txt,/tmp/doc2.txt]
     *
     *      Json
     *      List<File> files = Arrays.asList(new File(/tmp/doc1.txt),new File(/tmp/doc2.txt));
     *      jsonConfiguration.setList("tempFiles",files);
     *      "tempFiles": [/tmp/doc1.txt,/tmp/doc2.txt]
     *  </pre>
     *
     * @param key   the key for the value to set
     * @param input List of value to set
     * @since {@code 0.1}
     */
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

    /**
     * Transforms the given value to the <code>List of E</code>
     * @param type type of list given object will be converted to.
     * @param value value that is converted.
     * @return List of E
     */
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

    /**
     * Transform given List to a List of String.
     * @param input List that is converted.
     * @param <E> generic type E
     * @return List of String.
     */
    @SuppressWarnings("unchecked")
    private <E> List<String> transformList(final List<E> input) {
        final Converter<E> converter = (Converter<E>) converterRegistry.getConverter(input.get(0).getClass());
        return new FunctionalCollection<E>(input).map(new UnaryFunction<E, String>() {
            public String apply(final E input) {
                return converter.toString(input);
            }
        }).asList();
    }
    /**{@inheritDoc}*/
    public <E> void setList(final String key, final E... input) {
        setList(key, Arrays.asList(input));
    }

    /**
     * Converts the input to String, associate it with the given nested key and sets it in the configuration.
     *
     *
     *  <pre>
     *      Properties File
     *      propertyConfiguration.setNested("production.url","www.acme.com/rest");
     *      production.url=www.acme.com/rest
     *
     *      Yaml
     *      yamlConfiguration.setNested("production.url","www.acme.com/rest");
     *      production:
     *               url: www.acme.com/rest
     *
     *      Json
     *      jsonConfiguration.setNested("production.url","www.acme.com/rest");
     *      "production":{
     *               "url": www.acme.com/rest
     *             }
     *  </pre>
     *
     *
     * @param key   the key for the value to set
     * @param input value to set
     * @since {@code 0.1}
     */
    @SuppressWarnings("unchecked")
    public <E> void setNested(final String key, final E input) {
        Preconditions.checkNull(input, "input is null");
        final Converter<E> converter = (Converter<E>) converterRegistry.getConverter(input.getClass());
        final String value = converter.toString(input);
        setNestedStringValue(key, value);

    }

    /**
     * Converts all the values in input {@code List} to String, associate it with the given nested key and sets it in the configuration.
     *
     * <pre>
     *      Properties File
     *      List<File> files = Arrays.asList(new File(/tmp/doc1.txt),new File(/tmp/doc2.txt));
     *      propertyConfiguration.setNestedList("production.files",files);
     *      production.files=/tmp/doc1.txt,/tmp/doc2.txt
     *
     *      Yaml
     *      List<File> files = Arrays.asList(new File(/tmp/doc1.txt),new File(/tmp/doc2.txt));
     *      yamlConfiguration.setNestedList("production.files",files);
     *      production:
     *          files:[/tmp/doc1.txt,/tmp/doc2.txt]
     *
     *      Json
     *      List<File> files = Arrays.asList(new File(/tmp/doc1.txt),new File(/tmp/doc2.txt));
     *      jsonConfiguration.setNestedList("production.files",files);
     *      "production":{
     *          "files":[/tmp/doc1.txt,/tmp/doc2.txt]
     *          }
     *  </pre>
     * @param key   the key for the value to set
     * @param input List of value to set
     * @since {@code 0.1}
     */
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

    /**{@inheritDoc}*/
    public <E> void setNestedList(final String key, final E... input) {
        Preconditions.checkArgument(input != null && input.length > 0, "input is empty");
        setNestedList(key, Arrays.asList(input));
    }

    /**
     * Sets the given value using the nested key
     * @param key nested key
     * @param value value to be set
     */
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

    /**
     * Converts the value to String.
     * @param value value to be converted.
     * @return  String representation of the given value
     */
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

    /**
     * Returns the inner most map associated with the given nested key.
     * @param map properties map.
     * @param keys nested keys
     * @return inner most map tied to the given keys.
     */
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

}
