package com.mansoor.uncommon.configuration.Convertors;

import com.mansoor.uncommon.configuration.Configuration;
import com.mansoor.uncommon.configuration.TestUtil;
import com.mansoor.uncommon.configuration.util.Preconditions;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Muhammad Ashraf
 * @since 2/14/12
 */
public class FileConverterTest {
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = TestUtil.getPropertyConfiguration("/testProp.properties");
    }

    @Test
    public void testGetFile() throws Exception {
        final File result = configuration.get(File.class, "file1");
        assertNotNull("file is null", result);
    }

    @Test
    public void testGetFileList() throws Exception {
        final List<File> files = configuration.getList(File.class, "fileList");
        assertTrue("file list is empty or null", Preconditions.isNotEmpty(files));
        assertTrue("incorrect size", files.size() == 3);
    }

    @Test
    public void testSetFile() throws Exception {
        final File expected = new File("/home/test.txt");
        configuration.set(File.class, "fileKey", expected);
        final File result = configuration.get(File.class, "fileKey");
        assertEquals("files did not match", expected, result);
    }

    @Test
    public void testSetFileList() throws Exception {
        configuration.setList(File.class, "fileList", new File("a.txt"), new File("b.txt"), new File("c.txt"));
        final File file = configuration.get(File.class, "fileList");
        assertEquals("incorrect result!", new File("a.txt,b.txt,c.txt").getPath(), file.getPath());
        final List<File> result = configuration.getList(File.class, "fileList");
        assertTrue("result is empty", Preconditions.isNotEmpty(result));

    }
}
