package com.mansoor.uncommon.configuration.Convertors;

import java.util.HashMap;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public class DefaultConverterRegistry extends BaseConverterRegistry implements ConverterRegistry {
    public DefaultConverterRegistry() {
        super(new HashMap<Class<?>, Converter<?>>());
    }



    public <A> void addConverter(Class<A> type, Converter<A> converter) {
        converters.put(type, converter);
    }

    public <A> Converter<A> getConverter(Class<A> type) {
        return super.getConverter(type);
    }

    public void clear() {
        converters.clear();
    }
}
