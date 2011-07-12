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

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import org.springframework.beans.MutablePropertyValues;

public abstract class AbstractOptionHandler implements OptionHandler {
    private static final Set<Class<?>> booleanTypes;
    
    static {
        @SuppressWarnings("unchecked")
        Set<Class<?>> temp = unmodifiableSet(new HashSet<Class<?>>(asList(boolean.class, Boolean.class)));
        booleanTypes = temp;
    }
    
    protected final Option option;
    protected final String propertyName;
    protected final Class<?> valueType;
    
    public AbstractOptionHandler(Option option, String propertyName, Class<?> valueType) {
        this.option = option;
        this.propertyName = propertyName;
        this.valueType = valueType;
    }
    
    public boolean hasValue() {
        return !booleanTypes.contains(valueType);
    }

    public OptionSpecBuilder register(OptionParser parser) {
        OptionSpecBuilder builder = parser.acceptsAll(names(), option.description());
        if (hasValue()) {
            if (option.requiresValue()) {
                builder.withRequiredArg();
            } else {
                builder.withOptionalArg();
            }
        }
        return builder;
    }
    
    public boolean addPropertyValue(MutablePropertyValues propertyValues, OptionSet optionSet) {
        boolean result;
        if (hasValue()) {
            result = addPropertyValue(propertyValues, optionSet, option.shortName()) ||
                addPropertyValue(propertyValues, optionSet, option.longName());
        } else {
            propertyValues.add(propertyName, optionSet.has(option.shortName()) || optionSet.has(option.longName()));
            result = true;
        }
        return result;
    }
    
    private boolean addPropertyValue(MutablePropertyValues propertyValues, OptionSet optionSet, String optionName) {
        boolean result = false;
        if (optionSet.has(optionName)) {
            propertyValues.add(propertyName, optionSet.valueOf(optionName));
            result = true;
        }
        return result;
    }
    
    private Collection<String> names() {
        List<String> result = new ArrayList<String>(2);
        addIfNotEmpty(result, option.shortName());
        return result;
    }

    private static void addIfNotEmpty(List<String> result, String name) {
        if (name.length() > 0) {
            result.add(name);
        }
    }
}
