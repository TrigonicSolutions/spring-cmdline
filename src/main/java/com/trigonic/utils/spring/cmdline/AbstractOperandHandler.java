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

import java.util.List;

import joptsimple.OptionSet;

import org.springframework.beans.MutablePropertyValues;

public abstract class AbstractOperandHandler implements OperandHandler {
    protected final Operand operand;
    protected final String propertyName;
    protected final Class<?> valueType;
    
    public AbstractOperandHandler(Operand operand, String propertyName, Class<?> valueType) {
        this.operand = operand;
        this.propertyName = propertyName;
        this.valueType = valueType;
        if (operand.index() < 0) {
            throw new IllegalArgumentException("operand index cannot be < 0");
        }
    }
    
    public boolean addPropertyValue(MutablePropertyValues propertyValues, OptionSet optionSet) {
        boolean result = false;
        List<String> nonOptionArgs = optionSet.nonOptionArguments();
        if (operand.index() < nonOptionArgs.size()) {
            Object propertyValue = nonOptionArgs.get(operand.index());
            if (Iterable.class.isAssignableFrom(valueType) || valueType.isArray()) {
                propertyValue = nonOptionArgs.subList(operand.index(), nonOptionArgs.size());
            }
            propertyValues.add(propertyName, propertyValue);
            result = true;
        }
        return result;
    }
    
    public int getIndex() {
        return operand.index();
    }
    
    public String getName() {
        return propertyName;
    }
    
    public boolean isRequired() {
        return operand.required();
    }
    
    public Object getDescription() {
        return operand.description();
    }
}
