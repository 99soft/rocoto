/*
 *    Copyright 2009-2011 The Rocoto Team
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
package org.nnsoft.guice.rocoto.configuration;

import static com.google.inject.name.Names.named;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import org.nnsoft.guice.rocoto.configuration.resolver.PropertiesResolverProvider;
import org.nnsoft.guice.rocoto.configuration.traversal.ConfigurationReaderBuilder;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.binder.LinkedBindingBuilder;

/**
 * The ConfigurationModule simplifies the task of loading configurations in Google Guice.
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public abstract class ConfigurationModule implements Module {

    private Binder binder;

    /**
     * {@inheritDoc}
     */
    public final void configure(Binder binder) {
        if (this.binder != null) {
            throw new IllegalArgumentException("Re-entry is not allowed.");
        }

        this.binder = binder;

        try {
            this.configure();
        } finally {
            this.binder = null;
        }
    }

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    protected abstract void configure();

    /**
     * Add a configuration reader.
     *
     * @param configurationReader the configuration reader.
     */
    protected final void addConfigurationReader(ConfigurationReader configurationReader) {
        try {
            Iterator<Entry<String, String>> properties = configurationReader.readConfiguration();
            while (properties.hasNext()) {
                Entry<String, String> property = properties.next();
                LinkedBindingBuilder<String> bindingBuilder = this.binder.bind(Key.get(String.class, named(property.getKey())));

                PropertiesResolverProvider formatter = new PropertiesResolverProvider(property.getValue());
                if (formatter.containsKeys()) {
                    bindingBuilder.toProvider(formatter);
                } else {
                    bindingBuilder.toInstance(property.getValue());
                }
            }
        } catch (Exception e) {
            this.binder.addError(e);
        }
    }

    /**
     * Allows adding configuration files automatically by traversing a directory; while scanning the dir,
     * if the current analyzed file satisfies one of more of the given {@link ConfigurationReaderBuilder}s
     * requirements, then a related {@link ConfigurationReader} will be built based on the configuration
     * file and added in the readers list.
     *
     * @param configurationsDir The directory has to be traversed
     * @param builders The {@link ConfigurationReaderBuilder} list involved in the directory traversing
     */
    protected final void addConfigurationReader(File configurationsDir, ConfigurationReaderBuilder...builders) {
        if (configurationsDir == null) {
            this.binder.addError("'configurationsDir' argument can't be null");
            return;
        }

        if (!configurationsDir.exists()) {
            this.binder.addError("Impossible to load configurations directory '%s' because it doesn't exist", configurationsDir);
            return;
        }

        if (!configurationsDir.isDirectory()) {
            this.binder.addError("Impossible to traverse '%s' because it is not a directory", configurationsDir);
            return;
        }

        if (builders == null || builders.length == 0) {
            this.binder.addError("At least one ConfigurationReaderBuilder is required");
            return;
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
    }

}
