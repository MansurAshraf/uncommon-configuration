package com.mansoor.uncommon.configuration.Convertors;

import com.mansoor.uncommon.configuration.util.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Muhammad Ashraf
 * @since 0.2
 */
public class DateTimeConverter implements Converter<DateTime> {

    private DateTimeFormatter fmt;
    private final static String pattern = "MM/dd/yyyy";

    public DateTimeConverter() {
        fmt = DateTimeFormat.forPattern(pattern);
    }

    /**
     * Converts a {@link String} value {@link DateTime}. This method is called immediately after
     * a property value is read from a configuration.
     *
     * @param input value to be converted
     * @return converted value
     */
    public DateTime convert(String input) {
        return Preconditions.isNotNull(input) ? fmt.parseDateTime(input) : null;
    }

    /**
     * Converts type {@code DateTime} to {@code String}. This method is called before setting the
     * value to a configuration.
     *
     * @param input input to be converted
     * @return String
     */
    public String toString(DateTime input) {
        return Preconditions.isNotNull(input) ? input.toString(fmt) : null;
    }

    public void setPattern(String pattern) {
        DateTimeFormat.forPattern(pattern);
    }
}
