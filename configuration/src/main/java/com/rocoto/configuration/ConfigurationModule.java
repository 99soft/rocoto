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

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class ConfigurationModule extends AbstractModule {

    private static final String ENV_PREFIX = "env.";

    private final CompositeConfiguration configuration = new CompositeConfiguration();

    public void addSystemConfiguration() {
        this.configuration.addConfiguration(new SystemConfiguration());
    }

    public void addEnvironmentVariablesConfiguration() {
        for (Entry<String, String> envVar : System.getenv().entrySet()) {
            this.configuration.addProperty(ENV_PREFIX + envVar.getKey(), envVar.getValue());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
        Iterator<String> keys = configuration.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = configuration.getString(key);
            this.bindConstant().annotatedWith(Names.named(key)).to(value);
        }
    }

}
