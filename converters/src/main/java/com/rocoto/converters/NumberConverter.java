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

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.inject.TypeLiteral;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
@Converts({
    byte.class,
    byte[].class,
    Byte.class,
    Byte[].class,
    short.class,
    short[].class,
    Short.class,
    Short[].class,
    int.class,
    int[].class,
    Integer.class,
    Integer[].class,
    long.class,
    long[].class,
    Long.class,
    Long[].class,
    float.class,
    float[].class,
    Float.class,
    Float[].class,
    double.class,
    double[].class,
    Double.class,
    Double[].class,
    BigDecimal.class,
    BigDecimal[].class,
    BigInteger.class,
    BigInteger[].class
})
final class NumberConverter extends AbstractConverter {

    @Override
    protected final Object simpleConvert(String value, TypeLiteral<?> toType) {
        Class<?> targetType = (Class<?>) toType.getRawType();

        // Byte
        if (Byte.class == targetType) {
            return new Byte(value);
        }

        // Short
        if (Short.class == targetType) {
            return new Short(value);
        }

        // Integer
        if (Integer.class == targetType) {
            return new Integer(value);
        }

        // Long
        if (Long.class == targetType) {
            return new Long(value);
        }

        // Float
        if (Float.class == targetType) {
            return new Float(value);
        }

        // Double
        if (Double.class == targetType) {
            return new Double(value);
        }

        // BigDecimal
        if (BigDecimal.class == targetType) {
            return new BigDecimal(value);
        }

        // BigInteger
        if (BigInteger.class == targetType) {
            return new BigInteger(value);
        }

        throw new IllegalArgumentException("Impossible to convert '"
                + value
                + "' to '"
                + targetType.getName()
                + "'");
    }

}
