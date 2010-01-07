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

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
@Converts(Class.class)
public final class ClassConverter implements TypeConverter {

    /**
     * {@inheritDoc}
     */
    public Object convert(String value, TypeLiteral<?> toType) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            try {
                return classLoader.loadClass(value);
            } catch (ClassNotFoundException ex) {
                // Don't fail, carry on and try this class's class loader
            }
        }

        // Try this class's class loader
        classLoader = ClassConverter.class.getClassLoader();
        try {
            return classLoader.loadClass(value);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Impossible to load class '"
                    + value
                    + "'", e);
        }
    }

}
