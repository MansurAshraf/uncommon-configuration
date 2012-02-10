package com.mansoor.uncommon.configuration;

import java.io.File;
import java.io.InputStream;

/**
 * @author mansoor
 * @since 2/9/12
 */
public interface Configuration {
    <E> E get(Class<E> type,String key);
    void load(File file);
    void load(InputStream inputStream);
}
