package com.mansoor.uncommon.configuration;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;


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

    @Test
    public void testGetNested() throws Exception {
        final String nestedValue = configuration.getNested(String.class, "glossary.title");
        assertThat(nestedValue, equalTo("example glossary"));
    }

    @Test
    public void testGetNestedList() throws Exception {
        final List<String> list = configuration.getNestedList(String.class, "glossary.GlossDiv.GlossList.GlossEntry.GlossDef.GlossSeeAlso");
        assertThat(list, hasItems("GML", "XML"));

    }

    @Test
    public void testSaveList() throws Exception {
        final List<File> files = Arrays.asList(new File("/tmp/f1.txt"), new File("/temp/file2"));
        configuration.setList("files", files);
        final String tempLocation = System.getProperty("java.io.tmpdir");
        final File prop = configuration.save(tempLocation + File.separator + "newjson.json");
        Assert.assertNotNull(prop);
        assertTrue(prop.exists());
    }
}
