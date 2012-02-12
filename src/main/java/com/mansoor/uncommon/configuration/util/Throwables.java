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

package com.mansoor.uncommon.configuration.util;

import com.mansoor.uncommon.configuration.exceptions.ConverterNotFoundException;
import com.mansoor.uncommon.configuration.exceptions.PropertyConversionException;

/**
 * @author Muhammad Ashraf
 * @since 2/9/12
 */
public final class Throwables {
    private Throwables() {
    }

    public static void converterNotFoundException(final String msg, final Throwable throwable) {
        throw new ConverterNotFoundException(msg, throwable);
    }

    public static void converterNotFoundException(final String s) {
        throw new ConverterNotFoundException(s);
    }

    public static void propertyConversionException(final String msg, final Throwable throwable) {
        throw new PropertyConversionException(msg, throwable);
    }
}
