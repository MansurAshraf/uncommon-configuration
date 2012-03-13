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
 * ConverterRegistry acts as a converter repository and defines method to {@code add}, {@code retrieve} and {@code clear} {@link Converter} from the registry.
 * {@code ConverterRegistry} is responsible for providing appropriate {@code Converter} for a given type.
 *
 * @author Muhammad Ashraf
 * @since 0.1
 */
public interface ConverterRegistry {

    /**
     * Adds a converter to the registry keyed on a {@link Class} type. This converter will be used to transform
     * all instance of the given class type.
     * <p>
     *     For example once a {@code Float} converter is added to the Registry, it will be invoke for all the
     *     {@code Float} value being set and retrieved through a {@link com.mansoor.uncommon.configuration.Configuration}
     *
     * <pre>
     *   addConverter(Float.class, new FloatConverter());
     *   configuration.get(Foat.class, "floatValue");
     *    </pre>
     * </p>
     *
     * @param type Class for which this converter is configured
     * @param converter Converter that is being added
     */
    <A> void addConverter(Class<A> type, Converter<A> converter);

    /**
     * Returns a converter for the given type.
     * @param type Class for which this converter is configured
     * @return Converter
     * @throws com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException if no converter are found for this class
     */
    <A> Converter<A> getConverter(Class<A> type);

    /**
     * Clears all the {@code Converter} from the registry
     */
    void clear();

}
