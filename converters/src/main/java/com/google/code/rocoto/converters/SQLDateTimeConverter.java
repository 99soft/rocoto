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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.google.inject.TypeLiteral;
import com.google.inject.internal.MoreTypes;
import com.google.inject.spi.TypeConverter;

/**
 * Converter implementation for {@code java.sql.Date}, {@code java.sql.Time} and
 * {@code java.sql.Timestamp}.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class SQLDateTimeConverter implements TypeConverter {

    /**
     * {@inheritDoc}
     */
    public Object convert(String value, TypeLiteral<?> toType) {
        Class<?> type = MoreTypes.getRawType(toType.getType());

        // java.sql.Date
        if (type == Date.class) {
            try {
                return java.sql.Date.valueOf(value);
            } catch (Throwable t) {
                throw new IllegalArgumentException("String must be in JDBC format [yyyy-MM-dd] to create a java.sql.Date");
            }
        }

        // java.sql.Time
        if (type == Time.class) {
            try {
                return Time.valueOf(value);
            } catch (Throwable t) {
                throw new IllegalArgumentException("String must be in JDBC format [HH:mm:ss] to create a java.sql.Time");
            }
        }

        if (type == Timestamp.class) {
            try {
                return Timestamp.valueOf(value);
            } catch (Throwable t) {
                throw new IllegalArgumentException("String must be in JDBC format [yyyy-MM-dd HH:mm:ss.fffffffff] to create a java.sql.Timestamp");
            }
        }

        throw new IllegalArgumentException("Type '"
                + type.getName()
                + " not supported in this version");
    }

    @Override
    public String toString() {
        return "TypeConverter<java.sql.Date | java.sql.Time | java.sql.Timestamp>";
    }

}
