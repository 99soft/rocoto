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
 * @since 3.2
 * @version $Id$
 */
final class PropertiesIterator implements Iterator<Entry<String, String>>, PropertiesReader {

    private final String keyPrefix;

    private final Iterator<?> properties;

    public PropertiesIterator(Map<Object, Object> properties) {
        this(null, properties);
    }

    public PropertiesIterator(String keyPrefix, Map<? extends Object, ? extends Object> properties) {
        this.keyPrefix = keyPrefix;
        this.properties = properties.entrySet().iterator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return this.properties.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public Entry<String, String> next() {
        Entry<? extends Object, ? extends Object> next = (Entry<? extends Object, ? extends Object>) this.properties.next();
        String key = String.valueOf(next.getKey());
        if (this.keyPrefix != null && this.keyPrefix.length() > 0) {
            key = this.keyPrefix + key;
        }
        return new KeyValue(key, String.valueOf(next.getValue()));
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        // not needed in this version
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Entry<String, String>> read() throws Exception {
        return this;
    }

}
