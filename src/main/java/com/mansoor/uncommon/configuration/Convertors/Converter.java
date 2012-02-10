package com.mansoor.uncommon.configuration.Convertors;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public interface Converter<T> {

    /**
     * Converts a value to type T
     * @param input value to be converted
     * @return converted value
     */
    T convert(String input);
}
