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

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
@Converts({ char.class, Character.class })
public final class CharacterConverter implements TypeConverter {

    /**
     * The prefix that identifies a string as being a Unicode character sequence.
     */
    private static final String UNICODE_PREFIX = "\\u";

    /**
     * The length of a Unicode character sequence.
     */
    private static final int UNICODE_LENGTH = 6;

    /**
     * 
     */
    private static final int UNICODE_RADIX = 16;

    /**
     * {@inheritDoc}
     */
    public Object convert(String value, TypeLiteral<?> toType) {
        if (value.length() == 0) {
            throw new IllegalArgumentException("empty value cannot be converted to char type");
        } else if (value.startsWith(UNICODE_PREFIX) && value.length() == UNICODE_LENGTH) {
            int code = Integer.parseInt(value.substring(UNICODE_PREFIX.length()), UNICODE_RADIX);
            return new Character((char) code);
        } if (value.length() != 1) {
            throw new IllegalArgumentException("value '"
                    + value
                    + "' with length "
                    + value.length()
                    + " cannot be converted to char type");
        }
        return new Character(value.charAt(0));
    }

}
