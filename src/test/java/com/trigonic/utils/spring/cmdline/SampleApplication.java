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

import static java.lang.System.out;

import java.io.File;

/**
 * The command-line application under Spring Command Line is a bean initialized in the usual Spring way.
 * Values are pulled from the command-line under guidance of annotations on bean properties.
 */
public class SampleApplication implements Runnable {
    private boolean verbose;
    private File file;
    private String[] messages;

    /**
     * Flag options are set to true if they are specified on the command line.
     */
    @Option(shortName = "v", longName = "verbose", description = "verbose messages")
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Options with values are converted by Spring in the normal way into the necessary property type.
     */
    @Option(shortName = "f", longName = "file", description = "file")
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Operands are specified similarly.
     */
    @Operand(index = 0, description = "messages")
    public void setMessage(String[] messages) {
        this.messages = messages;
    }

    /**
     * Simple way to run the application is the CommandLineAppContext's run method, which manages
     * the lifecycle of the application and invokes the bean's Runnable.run method.  This example
     * loads the applicationContext.xml file from the classpath into a parent application context
     * used by the one that loads the application.
     */
    public static void main(String[] args) {
        new CommandLineAppContext().run(SampleApplication.class, args);
    }

    public void run() {
        if (verbose) {
            out.println("Verbose!");
        }
        if (file != null) {
            out.println("File: " + file);
        }
        for (String message : messages) {
            out.println(message);
        }
    }
}
