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
package com.googlecode.rocoto.simpleconfig;

import java.util.Map.Entry;

/**
 * 
 * @author Simone Tripodi
 * @since 3.2
 * @version $Id$
 */
final class KeyValue implements Entry<String, String> {

    private final String key;

    private final String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getKey() {
        return this.key;
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     */
    public String setValue(String value) {
        // not needed in this version
        return null;
    }

}
