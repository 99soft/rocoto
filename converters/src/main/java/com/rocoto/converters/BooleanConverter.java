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

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
@Converts({ boolean.class, boolean[].class, Boolean.class, Boolean[].class })
public final class BooleanConverter extends AbstractConverter {

    /**
     * The set of strings that are known to map to Boolean.TRUE.
     */
    private final String[] trueStrings = { "true", "yes", "y", "on", "1" };
 
    /**
     * The set of strings that are known to map to Boolean.FALSE.
     */
    private final String[] falseStrings = { "false", "no", "n", "off", "0" };

    @Override
    protected Object simpleConvert(String value, TypeLiteral<?> toType) {
        for (String trueString : this.trueStrings) {
            if (trueString.equals(value)) {
                return Boolean.TRUE;
            }
        }

        for (String falseString : this.falseStrings) {
            if (falseString.equals(value)) {
                return Boolean.FALSE;
            }
        }

        throw new IllegalArgumentException("Can't convert value '"
                + value
                + "' to a Boolean");
    }

}
