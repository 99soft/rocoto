/*
 *    Copyright 2009-2010 The 99 Software Foundation
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

import static org.nnsoft.guice.rocoto.configuration.readers.PropertiesIterator.newPropertiesIterator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;


/**
 * {@link Properties} reader implementation able to read configuration files from classpath, file system or URLs.
 *
 * Thois reader implementation support both {@code .properties} and {@code .xml} properties format.
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public final class PropertiesURLReader extends AbstractConfigurationReader {

    /**
     * The URL has to be open.
     */
    private final URL url;

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
        if (url == null) {
            throw new IllegalArgumentException("'url' argument can't be null");
        }
        this.url = url;
        this.isXML = isXML;
    }

    /**
     * {@inheritDoc}
     */
    public final Iterator<Entry<String, String>> readConfiguration() throws Exception {
        URLConnection connection = null;
        InputStream input = null;
        try {
            connection = this.url.openConnection();
            connection.setUseCaches(false);
            input = connection.getInputStream();

            Properties properties = new Properties();
            if (this.isXML) {
                properties.loadFromXML(input);
            } else {
                properties.load(input);
            }

            return newPropertiesIterator(getPrefix(), properties);
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
