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
package com.rocoto.simpleconfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
final class AntStyleProperties implements Map<String, String> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Map<String, Formatter> formatters = new HashMap<String, Formatter>();

    private final Map<String, String> data = new HashMap<String, String>();

    public void clear() {
        this.formatters.clear();
        this.data.clear();
    }

    public boolean containsKey(Object key) {
        return this.data.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.data.containsValue(value);
    }

    public Set<Entry<String, String>> entrySet() {
        return this.data.entrySet();
    }

    public String get(Object key) {
        return this.data.get(key);
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public Set<String> keySet() {
        return this.data.keySet();
    }

    public String put(String key, String value) {
        this.putValue(key, value);
        this.resolveVariables();
        return this.data.get(key);
    }

    public void putAll(Map<? extends String, ? extends String> t) {
        for (Entry<? extends String, ? extends String> entry : t.entrySet()) {
            this.putValue(entry.getKey(), entry.getValue());
        }
        this.resolveVariables();
    }

    public void putAll(Properties properties) {
        for (Entry<Object, Object> entry : properties.entrySet()) {
            this.putValue(entry.getKey().toString(), entry.getValue().toString());
        }
        this.resolveVariables();
    }

    private void putValue(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Null key/value not supported");
        }
        this.data.put(key, value);

        Formatter formatter = new Formatter(value);
        if (formatter.containsKeys()) {
            this.formatters.put(key, formatter);
        } else {
            if (this.formatters.containsKey(key)) {
                this.formatters.remove(key);
            }
        }
    }

    private void resolveVariables() {
        for (Entry<String, Formatter> entry : this.formatters.entrySet()) {
            this.putValue(entry.getKey(), entry.getValue().format(this.data));
        }
    }

    public String remove(Object key) {
        String value = this.data.get(key);
        this.data.remove(key);
        this.formatters.remove(key);
        return value;
    }

    public int size() {
        return this.data.size();
    }

    public Collection<String> values() {
        return this.data.values();
    }

}
