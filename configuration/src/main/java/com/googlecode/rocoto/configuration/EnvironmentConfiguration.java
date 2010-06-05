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
package com.googlecode.rocoto.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.MapConfiguration;

/**
 * The Environment Variables configuration implementation.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
final class EnvironmentConfiguration extends MapConfiguration {

    /**
     * The default {@code env.} prefix.
     */
    private static final String DEFAULT_ENV_PREFIX = "env.";

    /**
     * Build a new Environment Variables configuration where variables are
     * prefixed with the default prefix.
     */
    public EnvironmentConfiguration() {
        this(DEFAULT_ENV_PREFIX);
    }

    /**
     * Build a new Environment Variables configuration where variables are
     * prefixed with the user specified prefix prefix.
     * 
     * @param prefix the Environment Variables prefix.
     */
    public EnvironmentConfiguration(String prefix) {
        super(createEnvVars(prefix));
    }

    /**
     * Builds the Environment Variables map with the specified prefix.
     *
     * @param prefix he specified Environment Variable prefix.
     * @return the Environment Variables map.
     */
    private static Map<String, String> createEnvVars(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            throw new IllegalArgumentException("empty prefix not allowed");
        }

        if (prefix.charAt(prefix.length() - 1) != '.') {
            prefix += '.';
        }

        Map<String, String> envVars = new HashMap<String, String>(System.getenv().size());
        for (Entry<String, String> envVar : System.getenv().entrySet()) {
            envVars.put(DEFAULT_ENV_PREFIX + envVar.getKey(), envVar.getValue());
        }
        return envVars;
    }

}
