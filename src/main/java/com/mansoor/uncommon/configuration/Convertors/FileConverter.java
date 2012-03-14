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

import java.io.File;

/** Converts a {@code String} to {@code File} and vice versa.
 * @author Muhammad Ashraf
 * @since 0.1
 */
public class FileConverter implements Converter<File> {
    /**
     * Converts a {@code String} to type {@code File}
     *
     * @param input value to be converted
     * @return converted value
     */
    public File convert(final String input) {
        return Preconditions.isNotNull(input) ? new File(input) : null;
    }

    /**
     * Converts {@code File} to {@code String}
     *
     * @param input input to be converted
     * @return String
     */
    public String toString(final File input) {
        return Preconditions.isNotNull(input) ? input.getPath() : null;
    }
}
