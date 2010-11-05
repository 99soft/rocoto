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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;
import com.googlecode.rocoto.configuration.resolver.PropertiesResolverProvider;
import com.googlecode.rocoto.configuration.traversal.ConfigurationReaderBuilder;

/**
 * The ConfigurationModule simplifies the task of loading configurations in Google Guice.
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public final class ConfigurationModule extends AbstractModule {

    /**
     * The configuration readers list, in the user specified order.
     */
    private final List<ConfigurationReader> readers;

    /**
     * Create a new {@link ConfigurationModule} instance on top of
     * the configuration readers list, in the user specified order.
     *
     * @param readers the configuration readers list, in the user specified order.
     */
    private ConfigurationModule(List<ConfigurationReader> readers) {
        this.readers = readers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        for (ConfigurationReader configurationReader : this.readers) {
            try {
                Iterator<Entry<String, String>> properties = configurationReader.readConfiguration();
                while (properties.hasNext()) {
                    Entry<String, String> property = properties.next();
                    LinkedBindingBuilder<String> bindingBuilder = this.bind(Key.get(String.class, Names.named(property.getKey())));

                    PropertiesResolverProvider formatter = new PropertiesResolverProvider(property.getValue());
                    if (formatter.containsKeys()) {
                        bindingBuilder.toProvider(formatter);
                    } else {
                        bindingBuilder.toInstance(property.getValue());
                    }
                }
            } catch (Exception e) {
                this.addError(e);
            }
        }
    }

    /**
     * The {@link ConfigurationModule} Builder.
     */
    public static final class Builder {

        /**
         * The configuration readers list, in the user specified order.
         */
        private final List<ConfigurationReader> readers = new ArrayList<ConfigurationReader>();

        /**
         * Add a configuration reader.
         *
         * @param configurationReader the configuration reader.
         * @return this Builder instance.
         */
        public Builder addConfigurationReader(ConfigurationReader configurationReader) {
            this.readers.add(configurationReader);
            return this;
        }

        /**
         * Allows adding configuration files automatically by traversing a directory; while scanning the dir,
         * if the current analyzed file satisfies one of more of the given {@link ConfigurationReaderBuilder}s
         * requirements, then a related {@link ConfigurationReader} will be built based on the configuration
         * file and added in the readers list.
         *
         * @param configurationsDir the directory has to be traversed.
         * @param builders the {@link ConfigurationReaderBuilder} list involved in the directory traversing.
         * @return this Builder instance.
         */
        public Builder addConfigurationReader(File configurationsDir, ConfigurationReaderBuilder...builders) {
            if (configurationsDir == null) {
                throw new IllegalArgumentException("'configurationsDir' argument can't be null");
            }
            if (!configurationsDir.exists()) {
                throw new RuntimeException("Impossible to load configurations directory '"
                        + configurationsDir
                        + "' because it doesn't exist");
            }
            if (!configurationsDir.isDirectory()) {
                throw new RuntimeException("Impossible to traverse '"
                        + configurationsDir
                        + "' because it is not a directory");
            }
            if (builders == null || builders.length == 0) {
                throw new RuntimeException("At least one ConfigurationReaderBuilder is required");
            }

            for (File file : configurationsDir.listFiles()) {
                if (file.isDirectory()) {
                    this.addConfigurationReader(file, builders);
                } else {
                    for (ConfigurationReaderBuilder builder : builders) {
                        if (builder.accept(file)) {
                            this.addConfigurationReader(builder.create(file));
                        }
                    }
                }
            }

            return this;
        }

        /**
         * Create a new {@link ConfigurationModule} instance based on the set readers.
         *
         * @return a new {@link ConfigurationModule} instance based on the set readers.
         */
        public Module create() {
            return new ConfigurationModule(this.readers);
        }

    }

}
