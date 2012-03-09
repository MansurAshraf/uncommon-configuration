package com.mansoor.uncommon.configuration;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * @author Muhammad Ashraf
 * @since 3/9/12
 */
public class JsonConfigurationTest {
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = TestUtil.getJsonConfiguration("/test.json");
    }

    @Test
    public void testGet() throws Exception {
        final String value = configuration.get(String.class, "test");
        assertThat(value, equalTo("test String"));
    }
}
