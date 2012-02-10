package com.mansoor.uncommon.configuration.util;

import com.mansoor.uncommon.configuration.Exceptions.ConverterNotFoundException;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public class Throwables {
    private Throwables() {
    }

    public static void converterNotFoundException(final String msg, final Throwable throwable) {
        throw new ConverterNotFoundException(msg, throwable);
    }

    public static void converterNotFoundException(String s) {
        throw new ConverterNotFoundException(s);
    }
}
