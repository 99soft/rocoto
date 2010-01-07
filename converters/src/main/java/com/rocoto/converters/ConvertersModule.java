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

import java.util.ArrayList;
import java.util.List;

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

    private final List<TypeConverter> converters = new ArrayList<TypeConverter>();

    public ConvertersModule() {
        this.registerConverter(new URLTypeConverter());
        this.registerConverter(new URITypeConverter());
        this.registerConverter(new SQLDateConverter());
        this.registerConverter(new SQLTimeConverter());
        this.registerConverter(new SQLTimestampConverter());
        this.registerConverter(new ClassConverter());
        this.registerConverter(new FileConverter());
        this.registerConverter(new BooleanConverter());
        this.registerConverter(new CharacterConverter());
        this.registerConverter(new CharacterArrayConverter());
    }

    public void registerConverter(TypeConverter converter) {
        this.converters.add(converter);
    }

    public void registerConverters(List<TypeConverter> converters) {
        this.converters.addAll(converters);
    }

    @Override
    protected void configure() {
        for (TypeConverter converter : this.converters) {
            Class<? extends TypeConverter> converterClass = converter.getClass();
            if (!converterClass.isAnnotationPresent(Converts.class)) {
                this.addError("Converter '"
                        + converter.getClass().getName()
                        + "' has to be annotated with '@"
                        + Converts.class.getName()
                        + "'");
            } else {
                for (Class<?> target : converterClass.getAnnotation(Converts.class).value()) {
                    this.binder().convertToTypes(Matchers.only(TypeLiteral.get(target)), converter);
                }
            }
        }
    }

}
