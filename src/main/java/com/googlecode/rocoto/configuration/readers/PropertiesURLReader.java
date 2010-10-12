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
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public final class PropertiesURLReader extends AbstractConfigurationURLReader {

    private final boolean isXML;

    public PropertiesURLReader(File file) {
        this(file, false);
    }

    public PropertiesURLReader(File file, boolean isXML) {
        super(file);
        this.isXML = isXML;
    }

    public PropertiesURLReader(String classpathResource) {
        this(classpathResource, Thread.currentThread().getContextClassLoader());
    }

    public PropertiesURLReader(String classpathResource, ClassLoader classLoader) {
        this(classpathResource, classLoader, false);
    }

    public PropertiesURLReader(String classpathResource, ClassLoader classLoader, boolean isXML) {
        super(classpathResource, classLoader);
        this.isXML = isXML;
    }

    public PropertiesURLReader(URL url) {
        this(url, false);
    }

    public PropertiesURLReader(URL url, boolean isXML) {
        super(url);
        this.isXML = isXML;
    }

    @Override
    protected Iterator<Entry<String, String>> process(InputStream input) throws Exception {
        Properties properties = new Properties();
        if (this.isXML) {
            properties.loadFromXML(input);
        } else {
            properties.load(input);
        }
        return PropertiesIterator.createNew(properties);
    }

}
