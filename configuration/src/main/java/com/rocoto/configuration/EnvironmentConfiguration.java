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
package com.rocoto.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.MapConfiguration;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
final class EnvironmentConfiguration extends MapConfiguration {

    private static final String ENV_PREFIX = "env.";

    public EnvironmentConfiguration() {
        super(createEnvVars());
    }

    private static Map<String, String> createEnvVars() {
        Map<String, String> envVars = new HashMap<String, String>(System.getenv().size());
        for (Entry<String, String> envVar : System.getenv().entrySet()) {
            envVars.put(ENV_PREFIX + envVar.getKey(), envVar.getValue());
        }
        return envVars;
    }

}
