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

package com.mansoor.uncommon.configuration.transformers;

/**
 * @author Muhammad Ashraf
 * @since 2/26/12
 */

import com.mansoor.uncommon.configuration.Convertors.Converter;
import com.mansoor.uncommon.configuration.Convertors.ConverterRegistry;
import com.mansoor.uncommon.configuration.functional.functions.UnaryFunction;
import com.mansoor.uncommon.configuration.util.Preconditions;

/**
 * Transformer class used to transform a String to a given type
 *
 * @param <E>
 */
public class PropertyTransformer<E> implements UnaryFunction<String, E> {
    private final Class<E> type;
    private final ConverterRegistry converterRegistry;

    public PropertyTransformer(final Class<E> type, final ConverterRegistry converterRegistry) {
        Preconditions.checkNull(type, "type is null");
        Preconditions.checkNull(converterRegistry, "registry is null");
        this.type = type;
        this.converterRegistry = converterRegistry;
    }

    public E apply(final String input) {
        final Converter<E> converter = converterRegistry.getConverter(type);
        return converter.convert(input);
    }
}
