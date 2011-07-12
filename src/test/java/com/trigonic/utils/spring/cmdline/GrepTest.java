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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class GrepTest {
    @Test
    public void ignoreCaseShort() {
        String[] args = { "-i", "foo", "bar" };
        Grep grep = new CommandLineAppContext().parseAndGet(Grep.class, args);
        assertTrue(grep.isIgnoreCase());
        assertNull(grep.getPatternFile());
    }
    
    @Test
    public void ignoreCaseAtEnd() {
        String[] args = { "foo", "bar", "-i" };
        Grep grep = new CommandLineAppContext().parseAndGet(Grep.class, args);
        assertTrue(grep.isIgnoreCase());
        assertNull(grep.getPatternFile());
    }
    
    @Test
    public void ignoreCaseLong() {
        String[] args = { "--ignore-case", "foo", "bar" };
        Grep grep = new CommandLineAppContext().parseAndGet(Grep.class, args);
        assertTrue(grep.isIgnoreCase());
        assertNull(grep.getPatternFile());
    }
    
    @Test
    public void patternFileMissingArgument() {
        String[] args = { "-f" };
        try {
            new CommandLineAppContext().parseAndGet(Grep.class, args);
            fail("excepted CommandLineException");
        } catch (CommandLineException e) {
            assertEquals("Option ['f', 'file'] requires an argument", e.getMessage());
        }
    }
    
    @Test
    public void patternFileShort() {
        // TODO: in a proper grep implementation, the pattern would not be collected if -f or -e options were specified
        String[] args = { "-f", "foo/bar", "foo", "bar" };
        Grep grep = new CommandLineAppContext().parseAndGet(Grep.class, args);
        assertFalse(grep.isIgnoreCase());
        assertEquals("foo/bar", grep.getPatternFile());
        assertEquals("foo", grep.getPattern());
        assertEquals(1, grep.getFiles().length);
        assertEquals("bar", grep.getFiles()[0]);
    }
    
    @Test
    public void patternFileLong() {
        // TODO: in a proper grep implementation, the pattern would not be collected if -f or -e options were specified
        String[] args = { "--file", "foo/bar", "foo", "bar" };
        Grep grep = new CommandLineAppContext().parseAndGet(Grep.class, args);
        assertFalse(grep.isIgnoreCase());
        assertEquals("foo/bar", grep.getPatternFile());
        assertEquals("foo", grep.getPattern());
        assertEquals(1, grep.getFiles().length);
        assertEquals("bar", grep.getFiles()[0]);
    }
    
    @Test
    public void patternFileLongEquals() {
        // TODO: in a proper grep implementation, the pattern would not be collected if -f or -e options were specified
        String[] args = { "--file=foo/bar", "foo", "bar" };
        Grep grep = new CommandLineAppContext().parseAndGet(Grep.class, args);
        assertFalse(grep.isIgnoreCase());
        assertEquals("foo/bar", grep.getPatternFile());
        assertEquals("foo", grep.getPattern());
        assertEquals(1, grep.getFiles().length);
        assertEquals("bar", grep.getFiles()[0]);
    }
    
    @Test
    public void multipleFiles() {
        String[] args = { "foo", "bar", "baz", "yotz" };
        Grep grep = new CommandLineAppContext().parseAndGet(Grep.class, args);
        assertFalse(grep.isIgnoreCase());
        assertEquals("foo", grep.getPattern());
        assertEquals(3, grep.getFiles().length);
        assertEquals("bar", grep.getFiles()[0]);
        assertEquals("baz", grep.getFiles()[1]);
        assertEquals("yotz", grep.getFiles()[2]);
    }
    
    @Test
    public void missingPattern() {
        String[] args = {};
        try {
            new CommandLineAppContext().parseAndGet(Grep.class, args);
            fail("excepted CommandLineException");
        } catch (CommandLineException e) {
            assertEquals("Operand [pattern] is required", e.getMessage());
        }
    }
    
    @Test
    public void missingFiles() {
        String[] args = { "foo" };
        try {
            new CommandLineAppContext().parseAndGet(Grep.class, args);
            fail("excepted CommandLineException");
        } catch (CommandLineException e) {
            assertEquals("Operand [files] is required", e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        new CommandLineAppContext().run(Grep.class, args);
    }
    
    static class Grep implements Runnable {
        @Option(shortName="f", longName="file", description="File containing patterns, one per line")
        private String patternFile;

        private boolean ignoreCase;
        
        @Operand(index=0, description="Pattern to search for")
        private String pattern;
        
        @Operand(index=1, description="Files to search")
        private String[] files;
        
        @Option(shortName="i", longName="ignore-case", description="Ignore letter case in search")
        public void setIgnoreCase(boolean caseignoreCase) {
            this.ignoreCase = caseignoreCase;
        }
        
        public boolean isIgnoreCase() {
            return ignoreCase;
        }
        
        public void setPatternFile(String patternFile) {
            this.patternFile = patternFile;
        }
        
        public String getPatternFile() {
            return patternFile;
        }
        
        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
        
        public String getPattern() {
            return pattern;
        }
        
        public void setFiles(String[] files) {
            this.files = files;
        }
        
        public String[] getFiles() {
            return files;
        }
        
        public void run() {
        }
    }
}
