package com.mansoor.uncommon.configuration.Exceptions;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public class ConverterNotFoundException extends RuntimeException{
    public ConverterNotFoundException(String s) {
        super(s);
    }

    public ConverterNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ConverterNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
