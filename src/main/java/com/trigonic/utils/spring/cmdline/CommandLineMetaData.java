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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import joptsimple.OptionParser;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;

public class CommandLineMetaData {
    private Map<Option, OptionHandler> options = new HashMap<Option, OptionHandler>();
    
    public CommandLineMetaData(Class<?> beanClass) {
        populateOptions(beanClass);
    }

    public void register(OptionParser parser) {
        for (OptionHandler optionAccessor : options.values()) {
            optionAccessor.register(parser);
        }
    }
    
    private void populateOptions(Class<?> beanClass) {
        populateOptionMethods(beanClass);
        populateOptionFields(beanClass);
    }

    private void populateOptionMethods(Class<?> beanClass) {
        for (Method method : beanClass.getDeclaredMethods()) {
            Option option = method.getAnnotation(Option.class);
            if (option != null) {
                PropertyDescriptor property = BeanUtils.findPropertyForMethod(method);
                if (property == null) {
                    throw new BeanDefinitionStoreException("@Option annotation cannot be applied to non-property methods");
                }
                options.put(option, new OptionPropertyHandler(option, property));
            }
        }
    }

    private void populateOptionFields(Class<?> beanClass) {
        for (Field field : beanClass.getDeclaredFields()) {
            Option option = field.getAnnotation(Option.class);
            if (option != null) {
                options.put(option, new OptionFieldHandler(option, field));
            }
        }
    }

    public Collection<OptionHandler> getOptionHandlers() {
        return options.values();
    }
}
