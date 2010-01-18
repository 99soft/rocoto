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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Pattern;

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

    private final Map<TypeLiteral<?>, TypeConverter> converters = new HashMap<TypeLiteral<?>, TypeConverter>();

    public ConvertersModule() {
        this.registerConverter(URL.class, new URLTypeConverter());
        this.registerConverter(URI.class, new URITypeConverter());
        this.registerConverter(File.class, new FileConverter());
        this.registerConverter(Charset.class, new CharsetConverter());
        this.registerConverter(Locale.class, new LocaleConverter());
        this.registerConverter(Pattern.class, new PatternConverter());
        this.registerConverter(Properties.class, new PropertiesConverter());

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

    public void registerConverter(Class<?> type, TypeConverter typeConverter) {
        this.registerConverter(TypeLiteral.get(type), typeConverter);
    }

    public void registerConverter(TypeLiteral<?> typeLiteral, TypeConverter typeConverter) {
        this.converters.put(typeLiteral, typeConverter);
    }

    @Override
    protected void configure() {
        for (Entry<TypeLiteral<?>, TypeConverter> converter : this.converters.entrySet()) {
            this.binder().convertToTypes(Matchers.only(converter.getKey()), converter.getValue());
        }
    }

}
