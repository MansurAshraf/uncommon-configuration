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

package com.mansoor.uncommon.configuration.functional.functions;

/**
 * @author Muhammad Ashraf
 * @since 2/7/12
 */
public interface BinaryFunction<A, B> {

    /**
     * Lamda of kind (A,B) -> B
     *
     * @param seed  seed value
     * @param input input
     * @return result of Binary operation
     */
    B apply(final B seed, final A input);
}
