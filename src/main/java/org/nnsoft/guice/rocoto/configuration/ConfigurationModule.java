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

import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;
import static com.google.inject.util.Providers.guicify;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.nnsoft.guice.rocoto.configuration.binder.ClassLoaderBindingBuilder;
import org.nnsoft.guice.rocoto.configuration.binder.PrefixBindingBuilder;
import org.nnsoft.guice.rocoto.configuration.binder.PropertyValueBindingBuilder;
import org.nnsoft.guice.rocoto.configuration.readers.MapReader;
import org.nnsoft.guice.rocoto.configuration.readers.PropertiesReader;
import org.nnsoft.guice.rocoto.configuration.readers.PropertiesURLReader;
import org.nnsoft.guice.rocoto.configuration.resolver.PropertiesResolverProvider;
import org.nnsoft.guice.rocoto.configuration.traversal.ConfigurationReaderBuilder;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.binder.LinkedBindingBuilder;

/**
 * The ConfigurationModule simplifies the task of loading configurations in Google Guice.
 *
 * @author Simone Tripodi
 * @since 4.0
 */
public abstract class ConfigurationModule implements Module {

    /**
     * The environment variable prefix, {@code env.}
     */
    private static final String ENV_PREFIX = "env.";

    private final Collection<ConfigurationReader> readers = new ArrayList<ConfigurationReader>();

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

            for (ConfigurationReader configurationReader : this.readers) {
                try {
                    Iterator<Entry<String, String>> properties = configurationReader.readConfiguration();
                    while (properties.hasNext()) {
                        Entry<String, String> property = properties.next();
                        bindProperty(property.getKey()).toValue(property.getValue());
                    }
                } catch (Exception e) {
                    this.binder.addError(e);
                }
            }
        } finally {
            this.binder = null;
        }
    }

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    protected abstract void configure();

    /**
     * Binds to a property with the given name.
     *
     * @param name The property name
     * @return The property value binder
     */
    protected PropertyValueBindingBuilder bindProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name cannot be null.");
        }

        return new PropertyValueBindingBuilder() {

            public void toValue(final String value) {
                if (value == null) {
                    throw new IllegalArgumentException(String.format("Null value not admitted for property '%s's", name));
                }

                LinkedBindingBuilder<String> bindingBuilder = binder.bind(get(String.class, named(name)));

                PropertiesResolverProvider formatter = new PropertiesResolverProvider(value);
                if (formatter.containsKeys()) {
                    bindingBuilder.toProvider(guicify(formatter));
                } else {
                    bindingBuilder.toInstance(value);
                }
            }

        };
    }

    /**
     * 
     * @param properties
     * @return
     */
    protected PrefixBindingBuilder addProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Parameter 'properties' must be not null");
        }

        return this.addConfigurationReader(new PropertiesReader(properties));
    }

    /**
     * 
     * @param classPathResource
     * @return
     */
    protected ClassLoaderBindingBuilder addClassPathResource(final String classPathResource) {
        if (classPathResource == null) {
            throw new IllegalArgumentException("parameter 'classPathResource' must not be null");
        }

        return new ClassLoaderBindingBuilder() {

            private boolean isXML = false;

            public ClassLoaderBindingBuilder inXMLFormat() {
                this.isXML = true;
                return this;
            }

            public PrefixBindingBuilder usingRocotoClassLoader() {
                return this.usingClassLoader(this.getClass().getClassLoader());
            }

            public PrefixBindingBuilder usingContextClassLoader() {
                return this.usingClassLoader(Thread.currentThread().getContextClassLoader());
            }

            public PrefixBindingBuilder usingClassLoader(ClassLoader classLoader) {
                if (classLoader == null) {
                    throw new IllegalArgumentException("parameter 'classLoader' must not be null");
                }

                String resourceURL = classPathResource;
                if ('/' == classPathResource.charAt(0)) {
                    resourceURL = classPathResource.substring(1);
                }

                URL url = classLoader.getResource(resourceURL);
                if (url == null) {
                    throw new IllegalArgumentException(
                            String.format("ClassPath resource '%s' not found, make sure it is in the ClassPath or you're using the right ClassLoader",
                                    classPathResource));
                }
                return addConfigurationReader(new PropertiesURLReader(url, this.isXML));
            }

        };
    }

    /**
     * 
     * @param properties
     * @return
     */
    protected PrefixBindingBuilder addProperties(Map<String, String> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Parameter 'properties' must be not null");
        }

        return this.addConfigurationReader(new MapReader(properties));
    }

    /**
     * Add the Environment Variables properties, prefixed by {@code env.}.
     */
    protected void addSystemProperties() {
        this.addProperties(System.getProperties());
    }

    /**
     * Add the System Variables properties.
     */
    protected void addEnvironmentVariables() {
        this.addProperties(System.getenv()).withPrefix(ENV_PREFIX);
    }

    /**
     * Add a configuration reader.
     *
     * @param configurationReader the configuration reader.
     */
    protected final PrefixBindingBuilder addConfigurationReader(final ConfigurationReader configurationReader) {
        if (configurationReader == null) {
            throw new IllegalArgumentException("Parameter 'configurationReader' must be not null");
        }

        this.readers.add(configurationReader);

        return new PrefixBindingBuilder() {

            public void withPrefix(String prefix) {
                if (prefix == null || prefix.length() == 0) {
                    throw new IllegalArgumentException("Parameter 'prefix' must be not null or not empty");
                }

                configurationReader.setPrefix(prefix);
            }

        };
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
