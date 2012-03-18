package com.mansoor.uncommon.configuration;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Muhammad Ashraf
 * @since 3/17/12
 */
public class PropertiesConfigurationTestWithSampleProperties {

    PropertyConfiguration configuration;
    private static final Logger logger= LoggerFactory.getLogger(JSONConfigurationTestWithSampleJson.class);

    @Before
    public void setUp() throws Exception {
     configuration=new PropertyConfiguration(5, TimeUnit.MINUTES);
     configuration.load(this.getClass().getResource("/sample.properties").getPath());
    }

    @Test
    public void testStopPolling() throws Exception {
        configuration.stopPolling();
    }

    @Test
    public void testGetpasswordExpirationAsString() throws Exception {
        String expiration = configuration.get(String.class, "development.passwordExpiration");
        assertThat(expiration,is(equalTo("03/12/2014")));
        logger.info("expiration = " + expiration);
    }

    @Test
    public void testGetpasswordExpirationAsDate() throws Exception {
        Date expiration = configuration.get(Date.class, "development.passwordExpiration");
        assertThat(expiration,notNullValue());
        logger.info("expiration = " + expiration);
    }

    @Test
    public void testGetUri() throws Exception {
        URI uri = configuration.get(URI.class, "development.url");
        assertThat(uri,is(equalTo(new URI("http://localhost:8080/demo"))));
        logger.info("url = " + uri);
    }

    @Test
    public void testMaxCount() throws Exception {
        Integer maxConnections = configuration.get(Integer.class, "development.maxConnection");
        assertThat(maxConnections,is(equalTo(2)));
        logger.info("maxConnections = " + maxConnections);
    }

    @Test
    public void testGetList() throws Exception {
        List<File> logs = configuration.getList(File.class, "development.logFiles");
        assertThat(logs,hasItems(new File("/logs/debug.log"),new File("/logs/error.log")));
        logger.info("logs = " + logs);

    }

    @Test
    public void testSetDate() throws Exception {
        Date today=new Date();
        configuration.set("today",today);
        configuration.setList("authorizedUsers","Bob","Jim","Alex","Joe");
        configuration.save("/tmp/date_"+today+".properties");
    }
}
