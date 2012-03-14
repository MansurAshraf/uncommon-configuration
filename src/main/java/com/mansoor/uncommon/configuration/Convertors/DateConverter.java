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

import com.mansoor.uncommon.configuration.exceptions.PropertyConversionException;
import com.mansoor.uncommon.configuration.util.Preconditions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converts a Date to a String and vice versa.
 * @author Muhammad Ashraf
 * @since 0.1
 */
public class DateConverter implements Converter<Date> {
    /**
     * Default Date format
     */
    private final static String default_format = "MM/dd/yyyy";
    /**
     * Date format instance that will be used to formatting the date.
     */
    private DateFormat df;

    public DateConverter() {
        this.df = new SimpleDateFormat(default_format);
    }

    public DateConverter(final String format) {
        setDateFormat(format);
    }

    /**
     * Converts a {@code String} to a {@code Date} based on the Date Format.
     *
     * @param input value to be converted
     * @return converted value
     */
    public Date convert(final String input) {
        return Preconditions.isNotNull(input) ? parseDate(input) : null;
    }

    /**
     * Converts a {@code Date} to {@code String}
     *
     * @param input input to be converted
     * @return String
     */
    public String toString(final Date input) {
        return Preconditions.isNotNull(input) ? df.format(input) : null;
    }

    /**
     * Transforms a String to Date by using the configured {@link DateFormat}
     * @param input String that is converted to a Date
     * @return Date
     */
    private Date parseDate(final String input) {
        try {
            return df.parse(input);
        } catch (ParseException e) {
            throw new PropertyConversionException("conversion failed", e);
        }
    }


    /**
     * Sets DateFormat instance that will be used to format the date
     * @param format format that will be used during conversion.
     */
    public void setDateFormat(final String format) {
        Preconditions.checkBlank(format, "invalid format" + format);
        this.df = new SimpleDateFormat(format);
    }
}
