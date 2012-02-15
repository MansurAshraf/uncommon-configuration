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
import com.mansoor.uncommon.configuration.PropertyConfiguration;
import com.mansoor.uncommon.configuration.TestUtil;
import com.mansoor.uncommon.configuration.exceptions.PropertyConversionException;
import com.mansoor.uncommon.configuration.util.Preconditions;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Muhammad Ashraf
 * @since 2/12/12
 */
public class DateConverterTest {
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = TestUtil.getPropertyConfiguration("/testProp.properties");
    }

    @Test
    public void testGetDate() throws Exception {
        final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final Date result = dateFormat.parse("02/23/2012");

        final Date dateOne = configuration.get(Date.class, "dateOne");
        assertEquals("incorrect date", result, dateOne);
    }

    @Test
    public void testCustomFormat() throws Exception {
        configuration.getConverterRegistry().addConverter(Date.class, new DateConverter("MM-dd-yyyy"));
        final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        final Date result = dateFormat.parse("02-12-2012");

        final Date dateOne = configuration.get(Date.class, "dateTwo");
        assertEquals("incorrect date", result, dateOne);

    }

    @org.junit.Test(expected = PropertyConversionException.class)
    public void testInvalidData() throws Exception {
        configuration.get(Date.class, "invalidDate");
    }

    @Test
    public void testGetDateList() throws Exception {
        final List<Date> result = configuration.getList(Date.class, "dateList");
        assertFalse("result is empty", Preconditions.isEmpty(result));
        assertTrue("incorrect size", result.size() == 4);

    }

    @Test
    public void testGetDateListWithCustomSeparator() throws Exception {
        ((PropertyConfiguration) configuration).setDeliminator(' ');
        final List<Date> result = configuration.getList(Date.class, "dateList2");
        assertFalse("result is empty", Preconditions.isEmpty(result));
        assertTrue("incorrect size", result.size() == 4);
    }

    @Test
    public void testGetDateListWithCustomSeparatorAndCustomFormat() throws Exception {
        configuration.getConverterRegistry().addConverter(Date.class, new DateConverter("MM-dd-yyyy"));
        ((PropertyConfiguration) configuration).setDeliminator(' ');
        final List<Date> result = configuration.getList(Date.class, "dateList3");
        assertFalse("result is empty", Preconditions.isEmpty(result));
        assertTrue("incorrect size", result.size() == 4);
    }

    @Test
    public void testSetDate() throws Exception {
        final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final Date expectedDate = dateFormat.parse("02/23/2012");
        configuration.set("date4", expectedDate);
        final Date actualDate = configuration.get(Date.class, "date4");
        assertEquals("dates did not match", actualDate, expectedDate);

    }
}
