package com.mansoor.uncommon.configuration;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Muhammad Ashraf
 * @since 3/17/12
 */
public class YAMLConfigurationTestWithSampleYAML {

    Configuration configuration;
    private static final Logger logger= LoggerFactory.getLogger(JSONConfigurationTestWithSampleJson.class);

    @Before
    public void setUp() throws Exception {
        configuration=new YamlConfiguration (5, TimeUnit.MINUTES);
        configuration.load(this.getClass().getResource("/sample.yaml").getPath());
    }



    @Test
    public void testGetpasswordExpirationAsString() throws Exception {
        String description = configuration.get(String.class, "description");
        assertThat(description,is(equalTo("development environment")));
        logger.info("description = " + description);
    }

    @Test
    public void testGetNestedInteger() throws Exception {
        Integer maxConnection = configuration.getNested(Integer.class, "development.maxConnection");
        assertThat(maxConnection,is(equalTo(2)));
        logger.info("maxConnection = " + maxConnection);
    }

    @Test
    public void testNestedListAsString() throws Exception {
        String logFiles = configuration.getNested(String.class, "development.logFiles");
        assertThat(logFiles,notNullValue());
        logger.info("logFiles = " + logFiles);
    }

    @Test
    public void testNestedList() throws Exception {
        List<File> logFiles = configuration.getNestedList(File.class, "development.logFiles");
        assertThat(logFiles,notNullValue());
        logger.info("logFiles = " + logFiles);

    }

    @Test
    public void testName() throws Exception {
       configuration.setNested("books.book.java","Effective Java");
       configuration.setNested("books.book.ruby","Eloquent Ruby");
       List<String> authors= Arrays.asList("Joshua Bloch","Joshua Sureth");
        configuration.setNestedList("books.author",authors);
        configuration.save("/tmp/books.yaml");
    }
}
