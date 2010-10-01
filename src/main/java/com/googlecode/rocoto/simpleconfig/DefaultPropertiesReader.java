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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 * @since 3.2
 */
final class DefaultPropertiesReader implements PropertiesReader {

    private final String keyPrefix;

    private final Map<?, ?> properties;

    /**
     * 
     * @param properties
     */
    public DefaultPropertiesReader(Map<?, ?> properties) {
        this(null, properties);
    }

    /**
     * 
     * @param keyPrefix
     * @param properties
     */
    public DefaultPropertiesReader(String keyPrefix, Map<?, ?> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("argument 'properties' must not be null");
        }
        this.keyPrefix = keyPrefix;
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Entry<String, String>> read() throws Exception {
        return new PropertiesIterator(this.keyPrefix, this.properties);
    }

}
