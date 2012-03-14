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

package com.mansoor.uncommon.configuration.Convertors;

/**
 * The  {@code Converter} interface defines methods to transform a raw {@code String} value to generic value {@code T} and vice versa.
 * All {@link com.mansoor.uncommon.configuration.Configuration} implementations are responsible for calling appropriate converter for
 * a given type. A {@code Configuration} can be configured to use a {@code Converter} by adding it to  {@code Configuration's} {@link ConverterRegistry}
 *
 * <p>
 *     For example this is how a programmer will add a {@link FloatConverter} to {@link com.mansoor.uncommon.configuration.YamlConfiguration}
 *     <pre>
 *         Configuration configuration = new YamlConfiguration();
 *         configuration.getConverterRegistry().addConverter(Float.class, new FloatConverter());
 *     </pre>
 * </p>
 *
 * @author Muhammad Ashraf
 * @since 0.1
 */
public interface Converter<T> {

    /**
     * Converts a {@link String} value to type T. This method is called immediately after
     * a property value is read from a configuration.
     *
     * @param input value to be converted
     * @return converted value
     */
    T convert(String input);

    /**
     * Converts type {@code T} to {@code String}. This method is called before setting the
     * value to a configuration.
     *
     * @param input input to be converted
     * @return String
     */
    String toString(T input);
}
