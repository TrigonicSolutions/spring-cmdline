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

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class Grep implements Runnable {
    @Autowired(required=true)
    private ApplicationContext appContext;

    @Option(shortName="f", longName="file", description="File containing patterns, one per line")
    private File patternFile;

    private boolean ignoreCase;

    @Operand(index=0, description="Pattern to search for")
    private String pattern;

    @Operand(index=1, description="Files to search")
    private File[] files;

    @Option(shortName="i", longName="ignore-case", description="Ignore letter case in search")
    public void setIgnoreCase(boolean caseignoreCase) {
        this.ignoreCase = caseignoreCase;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setPatternFile(File patternFile) {
        this.patternFile = patternFile;
    }

    public File getPatternFile() {
        return patternFile;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public File[] getFiles() {
        return files;
    }

    public ApplicationContext getAppContext() {
        return appContext;
    }

    public static void main(String[] args) {
        ClassPathResource contextXml = new ClassPathResource("Grep-context.xml", Grep.class);
        new CommandLineAppContext(contextXml).run(Grep.class, args);
    }

    public void run() {
    }
}