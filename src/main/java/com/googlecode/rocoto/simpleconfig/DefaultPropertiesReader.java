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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 * @since 3.2
 */
final class DefaultPropertiesReader implements PropertiesReader {

    private final URL url;

    private final boolean isXML;

    /**
     * 
     * @param classpathResource
     * @param classLoader
     */
    public DefaultPropertiesReader(String classpathResource, ClassLoader classLoader, boolean isXML) {
        if (classpathResource == null) {
            throw new IllegalArgumentException("'classpathResource' argument can't be null");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("'classLoader' argument can't be null");
        }

        if ('/' == classpathResource.charAt(0)) {
            classpathResource = classpathResource.substring(1);
        }

        URL url = classLoader.getResource(classpathResource);
        if (url == null) {
            throw new IllegalArgumentException("classpath resource '"
                    + classpathResource
                    + "' doesn't exist");
        }
        this.url = url;
        this.isXML = isXML;
    }

    /**
     * 
     * @param file
     */
    public DefaultPropertiesReader(File file, boolean isXML) {
        if (file == null) {
            throw new IllegalArgumentException("'configurationFile' argument can't be null");
        }
        if (!file.exists()) {
            throw new RuntimeException("Impossible to read file '"
                    + file.getAbsolutePath()
                    + " because it doesn't exist");
        }

        try {
            this.url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Impossible to load properties file '"
                    + file.getAbsolutePath()
                    + ", see nested exceptions", e);
        }
        this.isXML = isXML;
    }

    /**
     * 
     * @param url
     */
    public DefaultPropertiesReader(URL url, boolean isXML) {
        if (url == null) {
            throw new IllegalArgumentException("'url' argument can't be null");
        }
        this.url = url;
        this.isXML = isXML;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Entry<String, String>> read() throws Exception {
        URLConnection connection = null;
        InputStream input = null;
        try {
            connection = url.openConnection();
            input = connection.getInputStream();

            Properties properties = new Properties();
            if (this.isXML) {
                properties.loadFromXML(input);
            } else {
                properties.load(input);
            }
            return new PropertiesIterator(properties);
        } finally {
            if (connection != null && (connection instanceof HttpURLConnection)) {
                ((HttpURLConnection) connection).disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // close quietly
                }
            }
        }
    }

}
