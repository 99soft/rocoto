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
package com.rocoto.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
public final class URLProcessor {

    private static final URLProcessor INSTANCE = new URLProcessor();

    public static URLProcessor getInstance() {
        return INSTANCE;
    }

    private URLProcessor() {
        // do nothing
    }

    /**
     * This class loader.
     */
    private final ClassLoader defaultClassLoader = this.getClass().getClassLoader();

    /**
     * 
     * @param classpathResource
     * @param streamProcessor
     */
    public void readClasspathResource(String classpathResource, InputStreamProcessor streamProcessor) {
        this.readClasspathResource(classpathResource, this.defaultClassLoader, streamProcessor);
    }

    /**
     * 
     * @param classpathResource
     * @param classLoader
     * @param streamProcessor
     */
    public void readClasspathResource(String classpathResource, ClassLoader classLoader, InputStreamProcessor streamProcessor) {
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
            throw new IllegalArgumentException("classpathResource '"
                    + classpathResource
                    + "' doesn't exist");
        }
    }

    public void readFile(File file, InputStreamProcessor streamProcessor) {
        if (file == null) {
            throw new IllegalArgumentException("'configurationFile' argument can't be null");
        }
        if (!file.exists()) {
            throw new RuntimeException("Impossible to read file '"
                    + file.getAbsolutePath()
                    + " because it doesn't exist");
        }

        try {
            this.readURL(file.toURL(), streamProcessor);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Impossible to load properties file '"
                    + file.getAbsolutePath()
                    + ", see nested exceptions", e);
        }
    }

    public void readURL(URL url, InputStreamProcessor streamProcessor) {
        if (url == null) {
            throw new IllegalArgumentException("'url' argument can't be null");
        }

        URLConnection connection = null;
        InputStream input = null;
        try {
            connection = url.openConnection();
            input = connection.getInputStream();

            streamProcessor.process(input);
        } catch (IOException e) {
            throw new RuntimeException("Impossible read URL "
                    + url
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

}
