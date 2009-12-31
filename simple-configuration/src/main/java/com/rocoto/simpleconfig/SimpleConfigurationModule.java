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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class SimpleConfigurationModule extends AbstractModule {

    private static final Pattern PROPERTIES_PATTERN = Pattern.compile(".*\\.properties", Pattern.CASE_INSENSITIVE);

    private static final Pattern XML_PROPERTIES_PATTERN = Pattern.compile(".*\\.xml", Pattern.CASE_INSENSITIVE);

    /**
     * The stored load configurations.
     */
    private final List<Properties> configuration = new ArrayList<Properties>();

    /**
     * This class loader.
     */
    private final ClassLoader thisClassLoader = this.getClass().getClassLoader();

    /**
     * Adds a {@link Properties} to the Guice Binder by loading the properties
     * file in the classpath.
     *
     * @param classpathConfigurationUrl
     */
    public void addProperties(String classpathConfigurationUrl) {
        this.addProperties(classpathConfigurationUrl, this.thisClassLoader);
    }

    public void addProperties(String classpathConfigurationUrl, ClassLoader classLoader) {
        this.addProperties(classpathConfigurationUrl, classLoader, false);
    }

    public void addXMLProperties(String classpathConfigurationUrl) {
        this.addProperties(classpathConfigurationUrl, this.thisClassLoader);
    }

    public void addXMLProperties(String classpathConfigurationUrl, ClassLoader classLoader) {
        this.addProperties(classpathConfigurationUrl, this.thisClassLoader, true);
    }

    private void addProperties(String classpathConfigurationUrl, ClassLoader classLoader, boolean isXML) {
        if (classpathConfigurationUrl == null) {
            throw new IllegalArgumentException("'classpathConfigurationUrl' argument can't be null");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("'classLoader' argument can't be null");
        }

        this.addProperties(classLoader.getResource(classpathConfigurationUrl), isXML);
    }

    public void addProperties(File configurationFile) {
        if (configurationFile == null) {
            throw new IllegalArgumentException("'configurationFile' argument can't be null");
        }
        if (!configurationFile.exists()) {
            throw new RuntimeException("Impossible to load properties file '"
                    + configurationFile
                    + " because it doesn't exist");
        }

        if (configurationFile.isDirectory()) {
            // if it is a directory, traverse it
            File[] childs = configurationFile.listFiles();
            if (childs == null || childs.length == 0) {
                // no need to traverse
                return;
            }
            for (File file : childs) {
                this.addProperties(file);
            }
            return;
        }

        boolean isXML = XML_PROPERTIES_PATTERN.matcher(configurationFile.getName()).matches();
        if (!isXML && !PROPERTIES_PATTERN.matcher(configurationFile.getName()).matches()) {
            // not *.xml and not *.properties, skipping file
            return;
        }

        try {
            this.addProperties(configurationFile.toURL(), isXML);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Impossible to load properties file '"
                    + configurationFile
                    + ", see nested exceptions", e);
        }
    }

    public void addProperties(URL configurationUrl) {
        this.addProperties(configurationUrl, false);
    }

    public void addXMLProperties(URL configurationUrl) {
        this.addProperties(configurationUrl, true);
    }

    private void addProperties(URL configurationUrl, boolean isXML) {
        if (configurationUrl == null) {
            throw new IllegalArgumentException("'configurationUrl' argument can't be null");
        }

        URLConnection connection = null;
        InputStream input = null;
        try {
            connection = configurationUrl.openConnection();
            input = connection.getInputStream();

            Properties properties = new Properties();
            if (isXML) {
                properties.loadFromXML(input);
            } else {
                properties.load(input);
            }
            this.addProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException("Impossible to open configuration URL "
                    + configurationUrl
                    + ", see nested exceptions", e);
        } finally {
            if (connection != null && (connection instanceof HttpURLConnection)) {
                ((HttpURLConnection) connection).disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void addProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("'properties' argument can't be null");
        }
        this.configuration.add(properties);
    }

    @Override
    protected void configure() {
        for (Properties properties : this.configuration) {
            Names.bindProperties(this.binder(), properties);
        }
    }

}
