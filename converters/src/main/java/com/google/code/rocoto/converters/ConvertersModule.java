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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;

/**
 * Allows user to easily register converters not included in the base
 * google-guice package.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class ConvertersModule extends AbstractModule {

    /**
     * Maintains all the auxiliary converters.
     */
    private final Map<Matcher<? super TypeLiteral<?>>, TypeConverter> converters =
        new HashMap<Matcher<? super TypeLiteral<?>>, TypeConverter>();

    /**
     * Builds a new converters with default converters.
     */
    public ConvertersModule() {
        this.registerConverter(BitSet.class, new BitSetConverter());
        this.registerConverter(Charset.class, new CharsetConverter());
        this.registerConverter(Currency.class, new CurrencyConverter());
        this.registerConverter(File.class, new FileConverter());
        this.registerConverter(Locale.class, new LocaleConverter());
        this.registerConverter(Pattern.class, new PatternConverter());
        this.registerConverter(Properties.class, new PropertiesConverter());
        this.registerConverter(TimeZone.class, new TimeZoneConverter());
        this.registerConverter(URL.class, new URLConverter());
        this.registerConverter(URI.class, new URIConverter());
        this.registerConverter(UUID.class, new UUIDConverter());

        NumberConverter numberConverter = new NumberConverter();
        this.registerConverter(BigDecimal.class, numberConverter);
        this.registerConverter(BigInteger.class, numberConverter);

        DateConverter dateConverter = new DateConverter();
        this.registerConverter(Calendar.class, dateConverter);
        this.registerConverter(Date.class, dateConverter);

        SQLDateTimeConverter sqlDateTimeConverter = new SQLDateTimeConverter();
        this.registerConverter(java.sql.Date.class, sqlDateTimeConverter);
        this.registerConverter(Time.class, sqlDateTimeConverter);
        this.registerConverter(Timestamp.class, sqlDateTimeConverter);
    }

    /**
     * Associates the specified converter with the specified type in this module.
     *
     * @param type type with which the specified converter is to be associated.
     * @param typeConverter converter to be associated with the specified type.
     */
    public void registerConverter(Class<?> type, TypeConverter typeConverter) {
        if (type == null) {
            throw new IllegalArgumentException("Argument 'type' nust not be null");
        }
        if (typeConverter == null) {
            throw new IllegalArgumentException("Argument 'typeConverter' nust not be null");
        }
        this.registerConverter(TypeLiteral.get(type), typeConverter);
    }

    /**
     * Associates the specified converter with the specified type literal in
     * this module.
     *
     * @param typeLiteral type literal with which the specified converter is to
     *        be associated.
     * @param typeConverter converter to be associated with the specified type.
     */
    public void registerConverter(TypeLiteral<?> typeLiteral, TypeConverter typeConverter) {
        if (typeLiteral == null) {
            throw new IllegalArgumentException("Argument 'typeLiteral' nust not be null");
        }
        if (typeConverter == null) {
            throw new IllegalArgumentException("Argument 'typeConverter' nust not be null");
        }
        this.registerConverter(Matchers.only(typeLiteral), typeConverter);
    }

    /**
     * Associates the specified converter with the specified matcher in
     * this module.
     *
     * @param matcher matcher with which the specified converter is to be
     *        associated.
     * @param typeConverter converter to be associated with the matcher.
     */
    public void registerConverter(Matcher<? super TypeLiteral<?>> matcher, TypeConverter typeConverter) {
        if (matcher == null) {
            throw new IllegalArgumentException("Argument 'matcher' nust not be null");
        }
        if (typeConverter == null) {
            throw new IllegalArgumentException("Argument 'typeConverter' nust not be null");
        }
        this.converters.put(matcher, typeConverter);
    }

    /**
     * Returns the converter to which the specified type is mapped, or
     * {@code null} if this module contains no mapping for the type.
     *
     * @param type the type whose associated converter is to be returned.
     * @return the converter to which the specified type is mapped, or null if
     *         this module contains no mapping for the type.
     */
    public TypeConverter lookup(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Argument 'type' nust not be null");
        }
        return this.lookup(TypeLiteral.get(type));
    }

    /**
     * Returns the converter to which the specified type literal is mapped, or
     * {@code null} if this module contains no mapping for the type.
     *
     * @param typeLiteral the type literal whose associated converter
     *        is to be returned.
     * @return the converter to which the specified type literal is mapped, or
     *         null if this module contains no mapping for the type.
     */
    public TypeConverter lookup(TypeLiteral<?> typeLiteral) {
        if (typeLiteral == null) {
            throw new IllegalArgumentException("Argument 'typeLiteral' nust not be null");
        }
        return this.lookup(Matchers.only(typeLiteral));
    }

    /**
     * Returns the converter to which the specified matcher is mapped, or
     * {@code null} if this module contains no mapping for the matcher.
     *
     * @param matcher the matcher whose associated converter is to be returned.
     * @return the converter to which the specified matcher is mapped, or
     *         null if this module contains no mapping for the matcher.
     */
    public TypeConverter lookup(Matcher<? super TypeLiteral<?>> matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("Argument 'matcher' nust not be null");
        }
        return this.converters.get(matcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        for (Entry<Matcher<? super TypeLiteral<?>>, TypeConverter> converter : this.converters.entrySet()) {
            this.binder().convertToTypes(converter.getKey(), converter.getValue());
        }
    }

}
