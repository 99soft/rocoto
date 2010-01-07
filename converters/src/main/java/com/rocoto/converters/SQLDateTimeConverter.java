package com.rocoto.converters;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.google.inject.TypeLiteral;

@Converts({
    Date.class,
    Date[].class,
    Time.class,
    Time[].class,
    Timestamp.class,
    Timestamp[].class
})
public final class SQLDateTimeConverter extends AbstractConverter {

    @Override
    protected Object simpleConvert(String value, TypeLiteral<?> toType) {
        Class<?> type = toType.getRawType();

        // java.sql.Date
        if (type == java.sql.Date.class) {
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

}
