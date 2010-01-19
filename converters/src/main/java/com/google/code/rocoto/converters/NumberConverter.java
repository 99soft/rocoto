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

import com.google.inject.TypeLiteral;
import com.google.inject.internal.MoreTypes;
import com.google.inject.spi.TypeConverter;

/**
 * Converter implementation for {@code java.math.BigDecimal} and
 * {@code java.math.BigInteger}.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
/**
 * {@inheritDoc}
 */
final class NumberConverter implements TypeConverter {

    /**
     * {@inheritDoc}
     */
    public Object convert(String value, TypeLiteral<?> toType) {
        Class<?> type = MoreTypes.getRawType(toType.getType());

        // BigDecimal
        if (BigDecimal.class == type) {
            return new BigDecimal(value);
        }

        // BigInteger
        if (BigInteger.class == type) {
            return new BigInteger(value);
        }

        throw new IllegalArgumentException("Impossible to convert '"
                + value
                + "' to '"
                + type.getName()
                + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TypeConverter<java.math.BigDecimal | java.math.BigInteger>";
    }

}
