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

import static org.nnsoft.guice.rocoto.configuration.internal.PropertiesIterator.newPropertiesIterator;

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
        return newPropertiesIterator(getPrefix(), properties);
    }

    @Override
    public String toString() {
        return super.toString() + (this.isXML ? "[XML]" : "");
    }

}
