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

import com.mansoor.uncommon.configuration.util.Preconditions;
import com.mansoor.uncommon.configuration.util.Throwables;

import java.util.Map;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public abstract class BaseConverterRegistry {
    protected final Map<Class<?>, Converter<?>> converters;

    public BaseConverterRegistry(Map<Class<?>, Converter<?>> converters) {
        this.converters = converters;
        loadDefaultConverters();
    }

    @SuppressWarnings(value = "unchecked")
    protected <T> Converter<T> getConverter(Class<T> type) {
        final Converter<T> converter = (Converter<T>) converters.get(type);
        if (Preconditions.isNull(converter)) {
            Throwables.converterNotFoundException("no converter found for " + type);
        }
        return converter;
    }

    protected void loadDefaultConverters() {
        converters.put(Integer.class, new IntegerConverter());
    }

}
