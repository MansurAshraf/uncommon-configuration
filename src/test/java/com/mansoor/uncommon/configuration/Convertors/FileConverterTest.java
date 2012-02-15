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
        configuration.set("fileKey", expected);
        final File result = configuration.get(File.class, "fileKey");
        assertEquals("files did not match", expected, result);
    }

    @Test
    public void testSetFileList() throws Exception {
        configuration.setList("fileList", new File("a.txt"), new File("b.txt"), new File("c.txt"));
        final File file = configuration.get(File.class, "fileList");
        assertEquals("incorrect result!", new File("a.txt,b.txt,c.txt").getPath(), file.getPath());
        final List<File> result = configuration.getList(File.class, "fileList");
        assertTrue("result is empty", Preconditions.isNotEmpty(result));

    }
}
