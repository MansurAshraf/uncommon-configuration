package com.mansoor.uncommon.configuration.Convertors;

import com.mansoor.uncommon.configuration.util.Preconditions;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public class IntegerConverter implements Converter<Integer> {
    /**
     * Converts a value to Integer
     *
     * @param input value to be converted
     * @return converted value
     */
    public Integer convert(String input) {
        return (Preconditions.isNotNull(input)) ? new Integer(input) : null;
    }
}
