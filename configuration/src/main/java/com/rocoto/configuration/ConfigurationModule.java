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

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
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

    public void loadFromXMLDefinition(File configurationFile) {
        if (configurationFile == null) {
            throw new IllegalArgumentException("'configurationFile' argument mustn't be null");
        }

        if (!configurationFile.exists()) {
            throw new IllegalArgumentException("Configuration file '"
                    + configurationFile.getAbsolutePath()
                    + "' doesn't exist");
        }

        if (configurationFile.isDirectory()) {
            throw new IllegalArgumentException("Impossible to load Configuration file '"
                    + configurationFile.getAbsolutePath()
                    + "' because it is a directory");
        }

        ConfigurationFactory configurationFactory = new ConfigurationFactory(configurationFile.getAbsolutePath());
        try {
            this.configuration.addConfiguration(configurationFactory.getConfiguration());
        } catch (ConfigurationException e) {
            throw new RuntimeException("Impossible to load the configuration from file '"
                    + configurationFile.getAbsolutePath()
                    + "'", e);
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
