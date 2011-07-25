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
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import joptsimple.OptionParser;
import joptsimple.internal.ColumnarData;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;

public class CommandLineMetaData {
    private Map<Option, OptionHandler> options = new HashMap<Option, OptionHandler>();
    private Map<Operand, OperandHandler> operands = new HashMap<Operand, OperandHandler>();
    
    public CommandLineMetaData(Class<?> beanClass) {
        populateOptionMethods(beanClass);
        populateOptionFields(beanClass);
        populateOperandMethods(beanClass);
        populateOperandFields(beanClass);
        validateOperands();
    }

    private void validateOperands() {
        int expectedIndex = 0;
        boolean remainderConsumed = false;
        for (Entry<Operand, OperandHandler> entry : operands.entrySet()) {
            if (remainderConsumed) {
                throw new IllegalArgumentException(String.format("Operand index [%d] illegally follows multi-value operand"));
            }
            Operand operand = entry.getKey();
            if (operand.index() < expectedIndex) {
                throw new IllegalArgumentException(String.format("Duplicate operand index [%d]", operand.index()));
            } else if (operand.index() > expectedIndex) {
                throw new IllegalArgumentException(String.format("Missing operand index [%d]", expectedIndex));
            }
            expectedIndex = operand.index() + 1;
            OperandHandler operandHandler = entry.getValue();
            remainderConsumed = operandHandler.hasMultipleValues();
        }
    }

    public Collection<OptionHandler> getOptionHandlers() {
        return options.values();
    }
    
    public Collection<OperandHandler> getOperandHandlers() {
        Set<OperandHandler> result = new TreeSet<OperandHandler>(new OperandHandlerOrderComparator());
        result.addAll(operands.values());
        return result;
    }

    public void register(OptionParser parser) {
        for (OptionHandler optionHandler : getOptionHandlers()) {
            optionHandler.register(parser);
        }
    }
    
    public void printOperandsOn(PrintStream err) {
        Collection<OperandHandler> operands = getOperandHandlers();
        if (operands.size() > 0) {
            ColumnarData columnarData = new ColumnarData("Operands", "Description");
            for (OperandHandler operand : operands) {
                columnarData.addRow(operand.getName(), operand.getDescription());
            }
            err.println();
            err.println(columnarData.format());
        }
    }

    private <T extends Annotation> void checkWriteableProperty(T annotation, Class<?> beanClass, Field field) {
        PropertyDescriptor property = BeanUtils.getPropertyDescriptor(beanClass, field.getName());
        if (property == null || property.getWriteMethod() == null) {
            throw new BeanDefinitionStoreException("@" + annotation.getClass().getSimpleName() + " annotation cannot be applied to fields without matching setters");
        }
    }

    private PropertyDescriptor getPropertyForMethod(String annotationType, Annotation annotation, Method method) {
        PropertyDescriptor property = BeanUtils.findPropertyForMethod(method);
        if (property == null) {
            throw new BeanDefinitionStoreException(annotationType + " annotation cannot be applied to non-property methods");
        }
        return property;
    }

    private void populateOperandFields(Class<?> beanClass) {
        Class<?> superClass = beanClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            populateOperandFields(superClass);
        }
        for (Field field : beanClass.getDeclaredFields()) {
            Operand operand = field.getAnnotation(Operand.class);
            if (operand != null) {
                checkWriteableProperty(operand, beanClass, field);
                operands.put(operand, new OperandFieldHandler(operand, field));
            }
        }
    }

    private void populateOperandMethods(Class<?> beanClass) {
        Class<?> superClass = beanClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            populateOperandMethods(superClass);
        }
        for (Method method : beanClass.getDeclaredMethods()) {
            Operand operand = method.getAnnotation(Operand.class);
            if (operand != null) {
                operands.put(operand, new OperandPropertyHandler(operand, getPropertyForMethod("@Operand", operand, method), beanClass));
            }
        }
    }

    private void populateOptionFields(Class<?> beanClass) {
        Class<?> superClass = beanClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            populateOptionFields(superClass);
        }
        for (Field field : beanClass.getDeclaredFields()) {
            Option option = field.getAnnotation(Option.class);
            if (option != null) {
                checkWriteableProperty(option, beanClass, field);
                options.put(option, new OptionFieldHandler(option, field));
            }
        }
    }

    private void populateOptionMethods(Class<?> beanClass) {
        Class<?> superClass = beanClass.getSuperclass();
        if (!superClass.equals(Object.class)) {
            populateOptionMethods(superClass);
        }
        for (Method method : beanClass.getDeclaredMethods()) {
            Option option = method.getAnnotation(Option.class);
            if (option != null) {
                options.put(option, new OptionPropertyHandler(option, getPropertyForMethod("@Option", option, method), beanClass));
            }
        }
    }
}
