/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trigonic.utils.spring.cmdline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class GrepTest {
    @Test
    public void ignoreCaseShort() {
        Grep grep = create("-i", "foo", "bar");
        assertTrue(grep._isIgnoreCase());
        assertNull(grep.getPatternFile());
    }

    @Test
    public void ignoreCaseAtEnd() {
        Grep grep = create("foo", "bar", "-i");
        assertTrue(grep._isIgnoreCase());
        assertNull(grep.getPatternFile());
    }
    
    @Test
    public void ignoreCaseLong() {
        Grep grep = create("--ignore-case", "foo", "bar");
        assertTrue(grep._isIgnoreCase());
        assertNull(grep.getPatternFile());
    }
    
    @Test
    public void patternFileMissingArgument() {
        try {
            create("-f");
            fail("excepted CommandLineException");
        } catch (CommandLineException e) {
            assertEquals("Option ['f', 'file'] requires an argument", e.getMessage());
        }
    }
    
    @Test
    public void patternFileShort() {
        // TODO: in a proper grep implementation, the pattern would not be collected if -f or -e options were specified
        Grep grep = create("-f", "foo/bar", "foo", "bar");
        assertFalse(grep._isIgnoreCase());
        assertEquals(new File("foo/bar"), grep.getPatternFile());
        assertEquals("foo", grep.getPattern());
        assertEquals(1, grep._getFiles().length);
        assertEquals(new File("bar"), grep._getFiles()[0]);
    }
    
    @Test
    public void patternFileLong() {
        // TODO: in a proper grep implementation, the pattern would not be collected if -f or -e options were specified
        Grep grep = create("--file", "foo/bar", "foo", "bar");
        assertFalse(grep._isIgnoreCase());
        assertEquals(new File("foo/bar"), grep.getPatternFile());
        assertEquals("foo", grep.getPattern());
        assertEquals(1, grep._getFiles().length);
        assertEquals(new File("bar"), grep._getFiles()[0]);
    }
    
    @Test
    public void patternFileLongEquals() {
        // TODO: in a proper grep implementation, the pattern would not be collected if -f or -e options were specified
        Grep grep = create("--file=foo/bar", "foo", "bar");
        assertFalse(grep._isIgnoreCase());
        assertEquals(new File("foo/bar"), grep.getPatternFile());
        assertEquals("foo", grep.getPattern());
        assertEquals(1, grep._getFiles().length);
        assertEquals(new File("bar"), grep._getFiles()[0]);
    }
    
    @Test
    public void multipleFiles() {
        Grep grep = create("foo", "bar", "baz", "yotz");
        assertFalse(grep._isIgnoreCase());
        assertEquals("foo", grep.getPattern());
        assertNull(grep.getPatternFile());
        assertEquals(3, grep._getFiles().length);
        assertEquals(new File("bar"), grep._getFiles()[0]);
        assertEquals(new File("baz"), grep._getFiles()[1]);
        assertEquals(new File("yotz"), grep._getFiles()[2]);
    }
    
    @Test
    public void missingPattern() {
        try {
            create();
            fail("excepted CommandLineException");
        } catch (CommandLineException e) {
            assertEquals("Operand [pattern] is required", e.getMessage());
        }
    }
    
    @Test
    public void missingFiles() {
        try {
            create("foo");
            fail("excepted CommandLineException");
        } catch (CommandLineException e) {
            assertEquals("Operand [files] is required", e.getMessage());
        }
    }
    
    @Test
    public void appContext() {
        ClassPathResource contextXml = new ClassPathResource("Grep-context.xml", Grep.class);
        CommandLineAppContext appContext = new CommandLineAppContext(contextXml);
        Grep grep = appContext.parseAndGet(Grep.class, new String[] {"foo", "bar"});
        assertSame(appContext, grep.getAppContext());
    }
    
    private Grep create(String... args) {
        ClassPathResource contextXml = new ClassPathResource("Grep-context.xml", Grep.class);
        return new CommandLineAppContext(contextXml).parseAndGet(Grep.class, args);
    }
}
