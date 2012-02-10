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
