package com.mansoor.uncommon.configuration.Convertors;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public interface ConverterRegistry {

    <A> void addConverter(Class<A> type,Converter<A> converter);
    <A> Converter<A> getConverter(Class<A> type);
    void clear();

}
