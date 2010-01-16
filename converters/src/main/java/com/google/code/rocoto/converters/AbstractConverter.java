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
package com.google.code.rocoto.converters;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.StringTokenizer;

import com.google.inject.TypeLiteral;
import com.google.inject.internal.MoreTypes;
import com.google.inject.spi.TypeConverter;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
public abstract class AbstractConverter implements TypeConverter {

    private static final String DEFAULT_DELIMITER = ",";

    public final Object convert(String value, TypeLiteral<?> toType) {
        if (GenericArrayType.class.isInstance(toType.getType())) {
            StringTokenizer tokenizer = new StringTokenizer(value, DEFAULT_DELIMITER);
            Class<?> arrayType = MoreTypes.getRawType(((GenericArrayType) toType.getType()).getGenericComponentType());
            Object array = Array.newInstance(arrayType, tokenizer.countTokens());

            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                Array.set(array, i++, this.simpleConvert(token, arrayType));
            }

            return array;
        }

        return this.simpleConvert(value, MoreTypes.getRawType(toType.getType()));
    }

    protected abstract Object simpleConvert(String value, Class<?> toType);

}
