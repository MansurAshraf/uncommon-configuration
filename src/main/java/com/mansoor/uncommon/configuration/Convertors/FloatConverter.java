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

import com.mansoor.uncommon.configuration.util.Preconditions;

/**
 * Converts a {@code String} to {@code Float} and vice versa
 * @author Muhammad Ashraf
 * @since 0.1
 */
public class FloatConverter implements Converter<Float> {
    /**
     * Converts a String to type Float
     *
     * @param input value to be converted
     * @return converted value
     */
    public Float convert(final String input) {
        return Preconditions.isNotNull(input) ? Float.valueOf(input) : null;
    }

    /**
     * Converts Float to String
     *
     * @param input input to be converted
     * @return String
     */
    public String toString(final Float input) {
        return Preconditions.isNotNull(input) ? String.valueOf(input) : null;
    }
}
