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

import java.util.StringTokenizer;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
public abstract class AbstractConverter implements TypeConverter {

    private static final String DEFAULT_DELIMITER = ",";

    public final Object convert(String value, TypeLiteral<?> toType) {
        if (((Class<?>) toType.getRawType()).isArray()) {
            StringTokenizer tokenizer = new StringTokenizer(value, DEFAULT_DELIMITER);
            Object[] array = new Object[tokenizer.countTokens()];

            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                array[i++] = this.simpleConvert(token, toType);
            }

            return array;
        }
        return this.simpleConvert(value, toType);
    }

    protected abstract Object simpleConvert(String value, TypeLiteral<?> toType);

}
