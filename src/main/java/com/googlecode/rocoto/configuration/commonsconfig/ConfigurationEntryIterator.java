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
package com.googlecode.rocoto.configuration.commonsconfig;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;

import com.googlecode.rocoto.configuration.internal.KeyValue;

/**
 * 
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
final class ConfigurationEntryIterator implements Iterator<Entry<String, String>> {

    private final Iterator<String> keysIterator;

    private final Configuration configuration;

    @SuppressWarnings("unchecked")
    public ConfigurationEntryIterator(Configuration configuration) {
        this.keysIterator = configuration.getKeys();
        this.configuration = configuration;
    }

    public boolean hasNext() {
        return this.keysIterator.hasNext();
    }

    public Entry<String, String> next() {
        String key = this.keysIterator.next();
        String value = this.configuration.getString(key);
        return new KeyValue(key, value);
    }

    public void remove() {
        // not needed in this version
    }

}
