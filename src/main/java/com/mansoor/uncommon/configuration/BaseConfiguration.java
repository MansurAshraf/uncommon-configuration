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

package com.mansoor.uncommon.configuration;

import com.mansoor.uncommon.configuration.Convertors.ConverterRegistry;
import com.mansoor.uncommon.configuration.functional.FunctionalCollection;
import com.mansoor.uncommon.configuration.transformers.PropertyTransformer;
import com.mansoor.uncommon.configuration.util.Preconditions;

import java.io.File;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Muhammad Ashraf
 * @since 2/25/12
 */
public abstract class BaseConfiguration {
    protected final ConverterRegistry converterRegistry;
    protected char deliminator = ',';
    protected final ReentrantLock lock = new ReentrantLock();
    protected File config;
    protected final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);
    protected Long lastModified;

    protected BaseConfiguration(final ConverterRegistry converterRegistry) {
        Preconditions.checkNull(converterRegistry, "ConverterRegistry is null");
        this.converterRegistry = converterRegistry;
    }

    /**
     * Sets the deliminator character that will be used to split the values
     *
     * @param deliminator deliminator
     */
    public void setDeliminator(final char deliminator) {
        this.deliminator = deliminator;
    }

    protected <E> List<E> getList(final Class<E> type, final String property) {
        List<E> result = null;
        if (Preconditions.isNotNull(property)) {
            result = new FunctionalCollection<String>(property.split(new String(new char[]{deliminator}))).map(new PropertyTransformer<E>(type, converterRegistry)).asList();
        }
        return result;
    }
}
