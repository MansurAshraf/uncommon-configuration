package com.mansoor.uncommon.configuration;

import static junit.framework.Assert.*;

import com.mansoor.uncommon.configuration.Exceptions.ConverterNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mansoor
 * @since 2/9/12
 */
public class PropertyConfigurationTest {

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration=new PropertyConfiguration();
        configuration.load(this.getClass().getResourceAsStream("/testProp.properties"));
    }

    @org.junit.Test(expected = ConverterNotFoundException.class)
    public void testConverterNotFoundException() throws Exception {
        final String name = configuration.get(String.class, "name");
    }

    @Test
    public void testGetInteger() throws Exception {
        final Integer one = configuration.get(Integer.class, "one");
        assertNotNull("Null value returned!",one);
        final Integer expected = 1;
        assertEquals("Incorrect value returned", expected,one);
    }

    @After
    public void tearDown() throws Exception {

    }
}
