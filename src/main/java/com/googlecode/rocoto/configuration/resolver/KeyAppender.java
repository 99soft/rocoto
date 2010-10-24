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
package com.googlecode.rocoto.configuration.resolver;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * {@link Appender} implementation that resolve the ${} variables
 * and append the result to the given buffer; if the variable
 * won't be resolved, the default value, if any, will be used,
 * otherwise 
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
final class KeyAppender implements Appender {

    /**
     * The key prefix, in its unresolved form.
     */
    private static final String KEY_PREFIX = "${";

    /**
     * The key has to be resolved.
     */
    private final String key;

    /**
     * The default value used if the key won't be resolved.
     */
    private final String defaultValue;

    /**
     * Creates a new KeyAppender with a property
     * key name and the default value.
     *
     * @param key the property key name.
     * @param defaultValue the property default value.
     */
    public KeyAppender(final String key, final String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public void append(StringBuilder buffer, Injector injector) {
        try {
            buffer.append(injector.getInstance(Key.get(String.class, Names.named(this.key))));
        } catch (Throwable e) {
            if (this.defaultValue != null) {
                buffer.append(this.defaultValue);
            } else {
                buffer.append(KEY_PREFIX).append(this.key).append('}');
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(KEY_PREFIX).append(this.key);
        if (this.defaultValue != null) {
            builder.append('|').append(this.defaultValue);
        }
        builder.append('}');
        return builder.toString();
    }

}
