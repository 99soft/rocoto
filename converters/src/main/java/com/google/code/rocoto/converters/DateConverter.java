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

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lombok.Setter;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
@Converts({
    Date.class,
    Date[].class,
    Calendar.class,
    Calendar[].class
})
public final class DateConverter extends AbstractConverter {

    private final List<String> patterns = new ArrayList<String>();

    @Setter
    private Locale locale;

    @Setter
    private TimeZone timeZone;

    public DateConverter() {
        this.addPattern("yyyy");
        this.addPattern("yyyy-MM");
        this.addPattern("yyyy-MM-dd");
        this.addPattern("yyyy-MM-dd'T'hh:mmZ");
        this.addPattern("yyyy-MM-dd'T'hh:mm:ssZ");
        this.addPattern("yyyy-MM-dd'T'hh:mm:ss.sZ");
    }

    public void addPattern(String pattern) {
        this.patterns.add(pattern);
    }

    @Override
    protected Object simpleConvert(String value, Class<?> toType) {
        Exception firstEx = null;
        for (String pattern : this.patterns) {
            try {
                DateFormat format = new SimpleDateFormat(pattern);
                format.setLenient(false);
                if (this.timeZone != null) {
                    format.setTimeZone(this.timeZone);
                }
                Date date = this.parse(value, format);

                if (Calendar.class == toType) {
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

        String errorMessage = "Error converting '"
            + value
            + "' using  patterns "
            + this.patterns;
        if (this.patterns.size() > 1) {
            throw new IllegalArgumentException(errorMessage);
        } else {
            throw new IllegalArgumentException(errorMessage, firstEx);
        }
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
            throw new IllegalArgumentException(msg);
        }

        return parsedDate;
    }

}
