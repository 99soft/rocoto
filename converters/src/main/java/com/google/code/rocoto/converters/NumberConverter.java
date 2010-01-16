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

import java.math.BigDecimal;
import java.math.BigInteger;

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
    protected final Object simpleConvert(String value, Class<?> toType) {
        // Byte
        if (Byte.class == toType) {
            return new Byte(value);
        }

        // Short
        if (Short.class == toType) {
            return new Short(value);
        }

        // Integer
        if (Integer.class == toType) {
            return new Integer(value);
        }

        // Long
        if (Long.class == toType) {
            return new Long(value);
        }

        // Float
        if (Float.class == toType) {
            return new Float(value);
        }

        // Double
        if (Double.class == toType) {
            return new Double(value);
        }

        // BigDecimal
        if (BigDecimal.class == toType) {
            return new BigDecimal(value);
        }

        // BigInteger
        if (BigInteger.class == toType) {
            return new BigInteger(value);
        }

        throw new IllegalArgumentException("Impossible to convert '"
                + value
                + "' to '"
                + toType.getName()
                + "'");
    }

}
