package com.mansoor.uncommon.configuration;

import com.mansoor.uncommon.configuration.Convertors.ConverterRegistry;
import com.mansoor.uncommon.configuration.Convertors.DefaultConverterRegistry;
import com.mansoor.uncommon.configuration.util.Preconditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author mansoor
 * @since 2/9/12
 */
public class PropertyConfiguration implements Configuration {
    private final ConverterRegistry converterRegistry;
    private final Properties properties = new Properties();

    public PropertyConfiguration() {
        this.converterRegistry = new DefaultConverterRegistry();
    }

    public PropertyConfiguration(final ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public <E> E get(Class<E> type, String key) {
        return converterRegistry.getConverter(type).convert(properties.getProperty(key));
    }

    public void load(final File propertyFile) {
        try {
            Preconditions.checkNull(propertyFile, "File is null");
            properties.load(new FileInputStream(propertyFile));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load file " + propertyFile, e);

        }
    }

    public void load(final InputStream inputStream){
        try {
            Preconditions.checkNull(inputStream, "InputStream is null");
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException( e);
        }
    }
}
