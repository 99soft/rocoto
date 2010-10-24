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
package com.googlecode.rocoto.configuration.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map.Entry;

import com.googlecode.rocoto.configuration.ConfigurationReader;

/**
 * Abstract reusable reader able to read configuration files from classpath, file system or URLs.
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public abstract class AbstractConfigurationURLReader implements ConfigurationReader {

    /**
     * The URL has to be open.
     */
    private final URL url;

    /**
     * Create a new reader of a classpath resource using the current thread classloader.
     *
     * @param classpathResource the classpath resource has to be read.
     */
    public AbstractConfigurationURLReader(String classpathResource) {
        this(classpathResource, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Create a new reader of a classpath resource using the given classloader.
     *
     * @param classpathResource the classpath resource has to be read.
     * @param classLoader the class loader to read the resource.
     */
    public AbstractConfigurationURLReader(String classpathResource, ClassLoader classLoader) {
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
    }

    /**
     * Create a new reader of a resource in the file system.
     *
     * @param file the resource in the file system.
     */
    public AbstractConfigurationURLReader(File file) {
        if (file == null) {
            throw new IllegalArgumentException("'configurationFile' argument can't be null");
        }
        if (!file.exists()) {
            throw new RuntimeException("Impossible to read file '"
                    + file.getAbsolutePath()
                    + " because it doesn't exist");
        }
        if (file.isDirectory()) {
            throw new RuntimeException("Impossible to read file '"
                    + file.getAbsolutePath()
                    + " because it is a directory");
        }

        try {
            this.url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Impossible to load properties file '"
                    + file.getAbsolutePath()
                    + ", see nested exceptions", e);
        }
    }

    /**
     * Create a new reader of a resource located in the given URL.
     *
     * @param url the URL that locates the configuration resource.
     */
    public AbstractConfigurationURLReader(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("'url' argument can't be null");
        }
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    public final Iterator<Entry<String, String>> readConfiguration() throws Exception {
        URLConnection connection = null;
        InputStream input = null;
        try {
            connection = url.openConnection();
            input = connection.getInputStream();

            return this.process(input);
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

    /**
     * Reads the configuration properties from the given input stream 
     *
     * @param input the stream from which the configuration has to be read.
     * @throws IOException if any error occurs while reading the stream.
     * @return  the configuration properties iterator.
     */
    protected abstract Iterator<Entry<String, String>> process(InputStream input) throws Exception;

}
