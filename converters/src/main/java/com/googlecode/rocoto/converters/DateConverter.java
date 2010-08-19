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
package com.googlecode.rocoto.converters;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;

/**
 * Converter implementation for {@code java.util.Calendar} and
 * {@code java.util.Date}.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class DateConverter extends AbstractConverter<Date> {

    private final List<String> patterns = new ArrayList<String>();

    private Locale locale;

    private TimeZone timeZone;

    public DateConverter() {
        // ISO date formats
        this.addPattern("yyyy");
        this.addPattern("yyyy-MM");
        this.addPattern("yyyy-MM-dd");
        this.addPattern("yyyy-MM-dd'T'hh:mmZ");
        this.addPattern("yyyy-MM-dd'T'hh:mm:ssZ");
        this.addPattern("yyyy-MM-dd'T'hh:mm:ss.sZ");
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public void addPattern(String pattern) {
        this.patterns.add(pattern);
    }

    /**
     * {@inheritDoc}
     */
    public Object convert(String value, TypeLiteral<?> toType) {
        Exception firstEx = null;
        for (String pattern : this.patterns) {
            try {
                DateFormat format;
                if (this.locale != null) {
                    format = new SimpleDateFormat(pattern, this.locale);
                } else {
                    format = new SimpleDateFormat(pattern);
                }
                if (this.timeZone != null) {
                    format.setTimeZone(this.timeZone);
                }
                format.setLenient(false);
                Date date = this.parse(value, format);

                if (Calendar.class == toType.getType()) {
                    Calendar calendar = null;
                    if (this.locale == null && this.timeZone == null) {
                        calendar = Calendar.getInstance();
                    } else if (this.locale == null) {
                        calendar = Calendar.getInstance(this.timeZone);
                    } else if (this.timeZone == null) {
                        calendar = Calendar.getInstance(this.locale);
                    } else {
                        calendar = Calendar.getInstance(this.timeZone, this.locale);
                    }
                    calendar.setTime(date);
                    calendar.setLenient(false);
                    return calendar;
                }

                return date;
            } catch (Exception ex) {
                if (firstEx == null) {
                    firstEx = ex;
                }
            }
        }

        throw new IllegalArgumentException("Error converting '"
            + value
            + "' using  patterns "
            + this.patterns, firstEx);
    }

    private Date parse(String value, DateFormat format) {
        ParsePosition pos = new ParsePosition(0);
        Date parsedDate = format.parse(value, pos); // ignore the result (use the Calendar)

        if (pos.getErrorIndex() >= 0
                || pos.getIndex() != value.length()
                || parsedDate == null) {
            String msg = "Error converting '"
                + value
                + "'";
            if (format instanceof SimpleDateFormat) {
                msg += " using pattern '"
                    + ((SimpleDateFormat) format).toPattern()
                    + "'";
            }
            throw new ProvisionException(msg);
        }

        return parsedDate;
    }

}
