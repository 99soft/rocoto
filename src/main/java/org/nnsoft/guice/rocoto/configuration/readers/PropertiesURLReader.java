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
package org.nnsoft.guice.rocoto.configuration.readers;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import org.nnsoft.guice.rocoto.configuration.internal.AbstractConfigurationURLReader;


/**
 * {@link Properties} reader implementation able to read configuration files from classpath, file system or URLs.
 *
 * Thois reader implementation support both {@code .properties} and {@code .xml} properties format.
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public final class PropertiesURLReader extends AbstractConfigurationURLReader {

    /**
     * Flag to mark properties are in XML format.
     */
    private final boolean isXML;

    /**
     * Create a new {@code .properties} reader of a in resource the file system.
     * 
     * @param file the resource in the file system.
     */
    public PropertiesURLReader(File file) {
        this(file, false);
    }

    /**
     * Create a new {@code .properties} reader of a in resource the file system.
     * 
     * @param file the resource in the file system.
     * @param isXML to mark if the properties file is in XML format or not.
     */
    public PropertiesURLReader(File file, boolean isXML) {
        super(file);
        this.isXML = isXML;
    }

    /**
     * Create a new {@code .properties} reader of a classpath resource using the current thread classloader.
     *
     * @param classpathResource the classpath resource has to be read.
     */
    public PropertiesURLReader(String classpathResource) {
        this(classpathResource, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Create a new {@code .properties} reader of a classpath resource using the current thread classloader.
     *
     * @param classpathResource the classpath resource has to be read.
     * @param isXML to mark if the properties file is in XML format or not.
     */
    public PropertiesURLReader(String classpathResource, boolean isXML) {
        this(classpathResource, Thread.currentThread().getContextClassLoader(), isXML);
    }

    /**
     * Create a new {@code .properties} reader of a classpath resource using the given classloader.
     *
     * @param classpathResource the classpath resource has to be read.
     * @param classLoader the class loader to read the resource.
     */
    public PropertiesURLReader(String classpathResource, ClassLoader classLoader) {
        this(classpathResource, classLoader, false);
    }

    /**
     * Create a new {@code .properties} reader of a classpath resource using the given classloader.
     *
     * @param classpathResource the classpath resource has to be read.
     * @param classLoader the class loader to read the resource.
     * @param isXML to mark if the properties file is in XML format or not.
     */
    public PropertiesURLReader(String classpathResource, ClassLoader classLoader, boolean isXML) {
        super(classpathResource, classLoader);
        this.isXML = isXML;
    }

    /**
     * Create a new {@code .properties} reader of a resource located in the given URL.
     *
     * @param url the URL that locates the configuration resource.
     */
    public PropertiesURLReader(URL url) {
        this(url, false);
    }

    /**
     * Create a new {@code .properties} reader of a resource located in the given URL.
     *
     * @param url the URL that locates the configuration resource.
     * @param isXML to mark if the properties file is in XML format or not.
     */
    public PropertiesURLReader(URL url, boolean isXML) {
        super(url);
        this.isXML = isXML;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<Entry<String, String>> process(InputStream input) throws Exception {
        Properties properties = new Properties();
        if (this.isXML) {
            properties.loadFromXML(input);
        } else {
            properties.load(input);
        }
        return PropertiesIterator.newPropertiesIterator(properties);
    }

    @Override
    public String toString() {
        return super.toString() + (this.isXML ? "[XML]" : "");
    }

}
