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
package com.googlecode.rocoto.simpleconfig;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;

/**
 * Simple configuration module to make easier the configuration properties to
 * Google Guice binder.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public class SimpleConfigurationModule extends AbstractModule {

    /**
     * The empty variable prefix
     */
    private static final String EMPY_PREFIX = "";

    /**
     * The environment variable prefix, {@code env.}
     */
    private static final String ENV_PREFIX = "env.";

    /**
     * The {@link System} property prefix, {@code env.}
     */
    private static final String SYSTEM_PREFIX = "sys.";

    /**
     * This class logger.
     */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * This class loader.
     */
    private final ClassLoader defaultClassLoader = this.getClass().getClassLoader();

    /**
     * The default file filter to traverse properties dirs.
     */
    private final AbstractPropertiesFileFilter defaultFileFilter = new DefaultPropertiesFileFilter();

    /**
     * The list of properties have to be read.
     */
    private final List<PropertiesReader> readers = new ArrayList<PropertiesReader>();

    /**
     * Flag to include Environment Variables, false by default.
     */
    private boolean addEnvironmentVariables = false;

    /**
     * Flag to include System Properties, false by default.
     */
    private boolean addSystemProperties = false;

    /**
     * Adds {@link Properties} to the Guice Binder by loading a classpath
     * resource file, using the default {@code ClassLoader}.
     *
     * @param classpathResource the classpath resource file.
     */
    public SimpleConfigurationModule addProperties(String classpathResource) {
        return this.addProperties(classpathResource, this.defaultClassLoader);
    }

    /**
     * Adds {@link Properties} to the Guice Binder by loading a classpath
     * resource file, using the user specified {@code ClassLoader}.
     *
     * @param classpathResource the classpath resource file.
     * @param classLoader the user specified {@code ClassLoader}.
     */
    public SimpleConfigurationModule addProperties(String classpathResource, ClassLoader classLoader) {
        return this.addProperties(classpathResource, classLoader, false);
    }

    /**
     * Adds XML {@link Properties} to the Guice Binder by loading a classpath
     * resource file, using the default {@code ClassLoader}.
     *
     * @param classpathResource the classpath resource file.
     */
    public SimpleConfigurationModule addXMLProperties(String classpathResource) {
        return this.addXMLProperties(classpathResource, this.defaultClassLoader);
    }

    /**
     * Adds XML {@link Properties} to the Guice Binder by loading a classpath
     * resource file, using the user specified {@code ClassLoader}.
     *
     * @param classpathResource the classpath resource file.
     * @param classLoader the user specified {@code ClassLoader}.
     */
    public SimpleConfigurationModule addXMLProperties(String classpathResource, ClassLoader classLoader) {
        return this.addProperties(classpathResource, this.defaultClassLoader, true);
    }

    /**
     * 
     * @param classpathResource
     * @param classLoader
     * @param isXML
     */
    private SimpleConfigurationModule addProperties(String classpathResource, ClassLoader classLoader, boolean isXML) {
        this.readers.add(new DefaultPropertiesReader(classpathResource, classLoader, isXML));
        return this;
    }

    /**
     * Adds {@link Properties} to the Guice Binder by loading a file; if the
     * user specified file is a directory, it will be traversed and every file
     * that matches with {@code *.properties} and {@code *.xml} patterns will be
     * load as properties file.
     *
     * @param configurationFile the properties file or the root dir has to be
     *        traversed.
     */
    public final SimpleConfigurationModule addProperties(File configurationFile) {
        return this.addProperties(configurationFile, this.defaultFileFilter);
    }

    /**
     * Adds {@link Properties} to the Guice Binder by loading a file; if the
     * user specified file is a directory, it will be traversed and every file
     * that matches with user specified patterns will be load as properties file.
     *
     * @param configurationFile the properties file or the root dir has to be
     *        traversed.
     * @param filter the user specified properties file patterns.
     */
    public final SimpleConfigurationModule addProperties(File configurationFile, AbstractPropertiesFileFilter filter) {
        if (configurationFile == null) {
            throw new IllegalArgumentException("'configurationFile' argument can't be null");
        }
        if (filter == null) {
            throw new IllegalArgumentException("'filter' argument can't be null");
        }

        if (!configurationFile.exists()) {
            throw new RuntimeException("Impossible to load properties file '"
                    + configurationFile
                    + " because it doesn't exist");
        }

        if (configurationFile.isDirectory()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Configuration file '"
                    + configurationFile.getAbsolutePath()
                    + "' is a directory, traversing it to look for properties file");
            }
            File[] childs = configurationFile.listFiles(filter);
            if (childs == null || childs.length == 0) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Configuration directory file '"
                            + configurationFile.getAbsolutePath()
                            + "' is empty");
                }
                return this;
            }
            for (File file : childs) {
                this.addProperties(file, filter);
            }
            return this;
        }

        this.readers.add(new DefaultPropertiesReader(configurationFile, filter.isXMLProperties(configurationFile)));
        return this;
    }

    /**
     * Adds {@link Properties} to the Guice Binder by loading data from a URL.
     *
     * @param configurationUrl the properties URL.
     */
    public final SimpleConfigurationModule addProperties(URL configurationUrl) {
        return this.addProperties(configurationUrl, false);
    }

    /**
     * Adds XML {@link Properties} to the Guice Binder by loading data from a URL.
     *
     * @param configurationUrl the properties URL.
     */
    public final SimpleConfigurationModule addXMLProperties(URL configurationUrl) {
        return this.addProperties(configurationUrl, true);
    }

    /**
     * 
     * @param configurationUrl
     * @param isXML
     */
    private final SimpleConfigurationModule addProperties(URL configurationUrl, boolean isXML) {
        this.readers.add(new DefaultPropertiesReader(configurationUrl, isXML));
        return this;
    }

    /**
     * Adds Java System properties, prefixed with {@code sys.}, to the Guice Binder.
     */
    public final SimpleConfigurationModule addSystemProperties() {
        this.addSystemProperties = true;
        return this;
    }

    /**
     * Adds environment variables, prefixed with {@code env.}, to the Guice Binder.
     */
    public final SimpleConfigurationModule addEnvironmentVariables() {
        this.addEnvironmentVariables = true;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        for (PropertiesReader reader : this.readers) {
            try {
                bindProperties(EMPY_PREFIX, reader.read(), this.binder());
            } catch (Exception e) {
                this.addError(e);
            }
        }
        if (this.addEnvironmentVariables) {
            bindProperties(ENV_PREFIX, System.getenv(), this.binder());
        }
        if (this.addSystemProperties) {
            bindProperties(SYSTEM_PREFIX, System.getProperties(), this.binder());
        }
    }

    private static void bindProperties(String keyPrefix, Map<? extends Object, ? extends Object> properties, Binder binder) {
        for (Entry<? extends Object, ? extends Object> property : properties.entrySet()) {
            String key = keyPrefix + String.valueOf(property.getKey());
            LinkedBindingBuilder<String> bindingBuilder = binder.bind(Key.get(String.class, Names.named(key)));

            String value = String.valueOf(property.getValue());
            Formatter formatter = new Formatter(value);
            if (formatter.containsKeys()) {
                bindingBuilder.toProvider(formatter);
            } else {
                bindingBuilder.toInstance(value);
            }
        }
    }

}
