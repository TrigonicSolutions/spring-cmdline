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

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;

public class CommandLineBeanDefinitionReader {
    private final BeanDefinitionRegistry registry;

    private final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    public CommandLineBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    public <T> void parse(Class<T> beanClass, String[] args) {
        OptionParser parser = new OptionParser();
        CommandLineMetaData metaData = new CommandLineMetaData(beanClass);
        metaData.register(parser);

        OptionSet optionSet;
        try {
            optionSet = parser.parse(args);

            CommandLineBeanDefinition beanDef = new CommandLineBeanDefinition(beanClass, metaData, optionSet);
            String beanName = beanNameGenerator.generateBeanName(beanDef, this.registry);

            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDef, beanName);
            BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
        } catch (OptionException e) {
            throw new CommandLineException(parser, metaData, e);
        } catch (OperandException e) {
            throw new CommandLineException(parser, metaData, e);
        }
    }
}
