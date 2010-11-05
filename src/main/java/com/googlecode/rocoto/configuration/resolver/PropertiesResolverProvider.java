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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * 
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public final class PropertiesResolverProvider implements Provider<String> {

    /**
     * The symbol that indicates a variable begin.
     */
    private static final String VAR_BEGIN = "$";

    /**
     * The symbol that separates the key name to the default value.
     */
    private static final String PIPE_SEPARATOR = "|";

    /**
     * The appenders list have to be invoked when resolving variables, in the given order.
     */
    private final List<Provider<String>> fragments = new ArrayList<Provider<String>>();

    /**
     * The list of resolvers that require the Injector reference.
     */
    private final List<VariableResolverProvider> resolvers = new ArrayList<VariableResolverProvider>();

    /**
     * Creates a new properties resolver instance.
     *
     * @param pattern the text fragment has to be parsed and extract keys.
     */
    public PropertiesResolverProvider(final String pattern) {
        int prev = 0;
        int pos;
        while ((pos = pattern.indexOf(VAR_BEGIN, prev)) >= 0) {
            if (pos > 0) {
                this.fragments.add(new TextFragmentProvider(pattern.substring(prev, pos)));
            }
            if (pos == pattern.length() - 1) {
                this.fragments.add(new TextFragmentProvider(VAR_BEGIN));
                prev = pos + 1;
            } else if (pattern.charAt(pos + 1) != '{') {
                if (pattern.charAt(pos + 1) == '$') {
                    this.fragments.add(new TextFragmentProvider(VAR_BEGIN));
                    prev = pos + 2;
                } else {
                    this.fragments.add(new TextFragmentProvider(pattern.substring(pos, pos + 2)));
                    prev = pos + 2;
                }
            } else {
                int endName = pattern.indexOf('}', pos);
                if (endName < 0) {
                    throw new IllegalArgumentException("Syntax error in property: " + pattern);
                }
                StringTokenizer keyTokenizer = new StringTokenizer(pattern.substring(pos + 2, endName), PIPE_SEPARATOR);
                String key = keyTokenizer.nextToken().trim();
                String defaultValue = null;
                if (keyTokenizer.hasMoreTokens()) {
                    defaultValue = keyTokenizer.nextToken().trim();
                }
                VariableResolverProvider variableResolver = new VariableResolverProvider(key, defaultValue);
                this.fragments.add(variableResolver);
                this.resolvers.add(variableResolver);
                prev = endName + 1;
            }
        }
        if (prev < pattern.length()) {
            this.fragments.add(new TextFragmentProvider(pattern.substring(prev)));
        }
    }

    /**
     * Return true, if the text contains at least one key in the ${} pattern,
     * false otherwise.
     *
     * @return true, if the text contains at least one key in the ${} pattern,
     *         false otherwise.
     */
    public boolean containsKeys() {
        return !this.resolvers.isEmpty();
    }

    /**
     * Set the Injector instance used to resolve variables.
     *
     * @param injector the Injector instance used to resolve variables.
     */
    @Inject
    public void setInjector(Injector injector) {
        for (VariableResolverProvider variableResolver : this.resolvers) {
            variableResolver.setInjector(injector);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String get() {
        StringBuilder buffer = new StringBuilder();
        for (Provider<String> appender : this.fragments) {
            buffer.append(appender.get());
        }
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.fragments.toString();
    }

}
