/*
 *    Copyright 2009-2010 The Rocoto Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.rocoto.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
public final class ConvertersModule extends AbstractModule {

    private final Map<Class<?>, TypeConverter> converters = new HashMap<Class<?>, TypeConverter>();

    public ConvertersModule() {
        this.registerConverter(new URLTypeConverter());
        this.registerConverter(new URITypeConverter());
        this.registerConverter(new SQLDateConverter());
        this.registerConverter(new SQLTimeConverter());
        this.registerConverter(new SQLTimestampConverter());
        this.registerConverter(new ClassConverter());
        this.registerConverter(new FileConverter());
        this.registerConverter(new BooleanConverter());
    }

    public void registerConverter(TypeConverter converter) {
        if (!converter.getClass().isAnnotationPresent(Converts.class)) {
            throw new IllegalArgumentException("Converter '"
                    + converter.getClass().getName()
                    + "' has to be annotated with '@"
                    + Converts.class.getName()
                    + "'");
        }

        for (Class<?> target : converter.getClass().getAnnotation(Converts.class).value()) {
            this.converters.put(target, converter);
        }
    }

    public void registerConverters(Map<Class<?>, TypeConverter> converters) {
        this.converters.putAll(converters);
    }

    @Override
    protected void configure() {
        for (Entry<Class<?>, TypeConverter> converter : this.converters.entrySet()) {
            this.binder().convertToTypes(Matchers.only(TypeLiteral.get(converter.getKey())), converter.getValue());
        }
    }

}
