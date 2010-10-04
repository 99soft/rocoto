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
package com.googlecode.rocoto.configuration.readers;

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
 * 
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public abstract class AbstractConfigurationURLReader<T> implements ConfigurationReader {

    private final URL url;

    /**
     * 
     * @param classpathResource
     * @param classLoader
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
     * 
     * @param file
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

        try {
            this.url = file.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Impossible to load properties file '"
                    + file.getAbsolutePath()
                    + ", see nested exceptions", e);
        }
    }

    /**
     * 
     * @param url
     */
    public AbstractConfigurationURLReader(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("'url' argument can't be null");
        }
        this.url = url;
    }

    /**
     * 
     * @return
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
     * 
     * @param input
     * @throws IOException
     */
    protected abstract Iterator<Entry<String, String>> process(InputStream input) throws Exception;

}
