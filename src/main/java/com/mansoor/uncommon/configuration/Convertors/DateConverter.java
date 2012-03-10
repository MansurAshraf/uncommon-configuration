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
 * @author Muhammad Ashraf
 * @since 2/12/12
 */
public class DateConverter implements Converter<Date> {
    private final static String default_format = "MM/dd/yyyy";
    private DateFormat df;

    public DateConverter() {
        this.df = new SimpleDateFormat(default_format);
    }

    public DateConverter(final String format) {
        setDateFormat(format);
    }

    /**
     * Converts a value to type T
     *
     * @param input value to be converted
     * @return converted value
     */
    public Date convert(final String input) {
        return Preconditions.isNotNull(input) ? parseDate(input) : null;
    }

    /**
     * Converts type T to String
     *
     * @param input input to be converted
     * @return String
     */
    public String toString(final Date input) {
        return Preconditions.isNotNull(input) ? df.format(input) : null;
    }

    private Date parseDate(final String input) {
        try {
            return df.parse(input);
        } catch (ParseException e) {
            throw new PropertyConversionException("conversion failed", e);
        }
    }

    public void setDateFormat(final String format) {
        Preconditions.checkBlank(format, "invalid format" + format);
        this.df = new SimpleDateFormat(format);
    }
}
