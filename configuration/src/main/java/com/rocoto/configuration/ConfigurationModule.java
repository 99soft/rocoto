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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Easy-to-use {@code Apache commons-configurations} wrapper,
 * built for users that require binding more complex configuration format.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class ConfigurationModule extends AbstractModule {

    private final Log log = LogFactory.getLog(this.getClass());

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final CompositeConfiguration configuration = new CompositeConfiguration();

    /**
     * This class loader.
     */
    private final ClassLoader defaultClassLoader = this.getClass().getClassLoader();

    public void addConfiguration(Configuration configuration) {
        this.configuration.addConfiguration(configuration);
    }

    public void loadConfiguration(Class<? extends FileConfiguration> configurationType, String classpathResource) {
        this.loadConfiguration(configurationType, classpathResource, UTF_8);
    }

    public void loadConfiguration(Class<? extends FileConfiguration> configurationType,
            String classpathResource,
            Charset encoding) {
        this.loadConfiguration(configurationType, classpathResource, this.defaultClassLoader, encoding);
    }

    public void loadConfiguration(Class<? extends FileConfiguration> configurationType,
            String classpathResource,
            ClassLoader classLoader) {
        this.loadConfiguration(configurationType,
                classpathResource,
                classLoader,
                UTF_8);
    }

    public void loadConfiguration(Class<? extends FileConfiguration> configurationType,
            String classpathResource,
            ClassLoader classLoader,
            Charset encoding) {
        if (classpathResource == null) {
            throw new IllegalArgumentException("'classpathConfigurationUrl' argument can't be null");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("'classLoader' argument can't be null");
        }

        if ('/' == classpathResource.charAt(0)) {
            classpathResource = classpathResource.substring(1);
        }

        URL url = classLoader.getResource(classpathResource);
        if (url == null) {
            throw new IllegalArgumentException("classpathResource '"
                    + classpathResource
                    + "' doesn't exist");
        }
        this.loadConfiguration(configurationType, url, encoding);
    }

    public void loadConfiguration(Class<? extends FileConfiguration> configurationType,
            File configurationFile) {
        this.loadConfiguration(configurationType, configurationFile, UTF_8);
    }

    public void loadConfiguration(Class<? extends FileConfiguration> configurationType,
            File configurationFile,
            Charset encoding) {
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

        try {
            this.loadConfiguration(configurationType, configurationFile.toURL(), encoding);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Impossible to load configuration file '"
                    + configurationFile.getAbsolutePath()
                    + ", see nested exceptions", e);
        }
    }

    public void loadConfiguration(Class<? extends FileConfiguration> configurationType, URL url, Charset encoding) {
        URLConnection connection = null;
        InputStream input = null;
        Reader reader = null;
        try {
            connection = url.openConnection();
            input = connection.getInputStream();
            reader = new InputStreamReader(input, encoding);

            FileConfiguration configuration = configurationType.newInstance();
            configuration.load(reader);
            this.configuration.addConfiguration(configuration);
        } catch (Exception e) {
            throw new RuntimeException("Impossible to open configuration URL "
                    + url
                    + ", see nested exceptions", e);
        } finally {
            if (connection != null && (connection instanceof HttpURLConnection)) {
                ((HttpURLConnection) connection).disconnect();
            }
            closeQuietly(input);
            closeQuietly(reader);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
        Iterator<String> keys = this.configuration.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = this.configuration.getString(key);

            if (this.log.isDebugEnabled()) {
                this.log.debug("Binding property '"
                        + key
                        + " = "
                        + value);
            }

            this.bindConstant().annotatedWith(Names.named(key)).to(value);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

}
