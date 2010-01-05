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
package com.rocoto.simpleconfig;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Simple configuration module to make easier the configuration properties to
 * Google Guice binder.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class SimpleConfigurationModule extends AbstractModule {

    /**
     * The default environment variable prefix, {@code env.}
     */
    private static final String DEFAULT_ENV_PREFIX = "env.";

    /**
     * This class logger.
     */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * The stored load configurations.
     */
    private final AntStyleProperties configuration = new AntStyleProperties();

    /**
     * This class loader.
     */
    private final ClassLoader defaultClassLoader = this.getClass().getClassLoader();

    /**
     * The default file filter to traverse properties dirs.
     */
    private final AbstractPropertiesFileFilter defaultFileFilter = new DefaultPropertiesFileFilter();

    /**
     * 
     */
    private final List<PropertiesReader> readers = new ArrayList<PropertiesReader>();

    /**
     * Adds {@link Properties} to the Guice Binder by loading a classpath
     * resource file, using the default {@code ClassLoader}.
     *
     * @param classpathResource the classpath resource file.
     */
    public void addProperties(String classpathResource) {
        this.addProperties(classpathResource, this.defaultClassLoader);
    }

    /**
     * Adds {@link Properties} to the Guice Binder by loading a classpath
     * resource file, using the user specified {@code ClassLoader}.
     *
     * @param classpathResource the classpath resource file.
     * @param classLoader the user specified {@code ClassLoader}.
     */
    public void addProperties(String classpathResource, ClassLoader classLoader) {
        this.addProperties(classpathResource, classLoader, false);
    }

    /**
     * Adds XML {@link Properties} to the Guice Binder by loading a classpath
     * resource file, using the default {@code ClassLoader}.
     *
     * @param classpathResource the classpath resource file.
     */
    public void addXMLProperties(String classpathResource) {
        this.addXMLProperties(classpathResource, this.defaultClassLoader);
    }

    /**
     * Adds XML {@link Properties} to the Guice Binder by loading a classpath
     * resource file, using the user specified {@code ClassLoader}.
     *
     * @param classpathResource the classpath resource file.
     * @param classLoader the user specified {@code ClassLoader}.
     */
    public void addXMLProperties(String classpathResource, ClassLoader classLoader) {
        this.addProperties(classpathResource, this.defaultClassLoader, true);
    }

    /**
     * 
     * @param classpathResource
     * @param classLoader
     * @param isXML
     */
    private void addProperties(String classpathResource, ClassLoader classLoader, boolean isXML) {
        this.readers.add(new PropertiesReader(classpathResource, classLoader, isXML));
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
    public void addProperties(File configurationFile) {
        this.addProperties(configurationFile, this.defaultFileFilter);
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
    public void addProperties(File configurationFile, AbstractPropertiesFileFilter filter) {
        if (configurationFile == null) {
            throw new IllegalArgumentException("'configurationFile' argument can't be null");
        }
        if (configurationFile == null) {
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
                return;
            }
            for (File file : childs) {
                this.addProperties(file, filter);
            }
            return;
        }

        this.readers.add(new PropertiesReader(configurationFile, filter.isXMLProperties(configurationFile)));
    }

    /**
     * Adds {@link Properties} to the Guice Binder by loading data from a URL.
     *
     * @param configurationUrl the properties URL.
     */
    public void addProperties(URL configurationUrl) {
        this.addProperties(configurationUrl, false);
    }

    /**
     * Adds XML {@link Properties} to the Guice Binder by loading data from a URL.
     *
     * @param configurationUrl the properties URL.
     */
    public void addXMLProperties(URL configurationUrl) {
        this.addProperties(configurationUrl, true);
    }

    /**
     * 
     * @param configurationUrl
     * @param isXML
     */
    private void addProperties(URL configurationUrl, boolean isXML) {
        this.readers.add(new PropertiesReader(configurationUrl, isXML));
    }

    /**
     * Adds Java System properties to the Guice Binder.
     */
    public void addSystemProperties() {
        this.addProperties(System.getProperties());
    }

    /**
     * Adds environment variables, prefixed with {@code env.}, to the Guice Binder.
     */
    public void addEnvironmentVariables() {
        this.addEnvironmentVariables(DEFAULT_ENV_PREFIX);
    }

    /**
     * Adds environment variables, prefixed with user specified prefix, to the
     * Guice Binder.
     *
     * @param prefix the user specified prefix.
     */
    public void addEnvironmentVariables(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            throw new IllegalArgumentException("empty prefix not allowed");
        }

        if (prefix.charAt(prefix.length() - 1) != '.') {
            prefix += '.';
        }

        for (Entry<String, String> envVar : System.getenv().entrySet()) {
            this.configuration.put(prefix + envVar.getKey(), envVar.getValue());
        }
    }

    /**
     * Adds already loaded {@link Properties} to the current configuration.
     *
     * @param properties the existing {@link Properties}.
     */
    public void addProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("'properties' argument can't be null");
        }
        this.configuration.putAll(properties);
    }

    /**
     * Adds an existing configuration to the current configuration.
     *
     * @param configuration the existing configuration.
     */
    public void addProperties(Map<String, String> configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("'configuration' argument can't be null");
        }
        this.configuration.putAll(configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        for (PropertiesReader reader : this.readers) {
            try {
                this.addProperties(reader.read());
            } catch (IOException e) {
                this.addError(e);
            }
        }
        Names.bindProperties(this.binder(), this.configuration);
    }

}
