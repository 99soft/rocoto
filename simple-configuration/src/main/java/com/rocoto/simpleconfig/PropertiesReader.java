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
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.rocoto.core.AbstractURLReader;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
final class PropertiesReader extends AbstractURLReader<Properties> {

    private final boolean isXML;

    public PropertiesReader(File file, boolean isXML) {
        super(file);
        this.isXML = isXML;
    }

    public PropertiesReader(String classpathResource, ClassLoader classLoader, boolean isXML) {
        super(classpathResource, classLoader);
        this.isXML = isXML;
    }

    public PropertiesReader(URL url, boolean isXML) {
        super(url);
        this.isXML = isXML;
    }

    @Override
    protected Properties process(InputStream input) throws Exception {
        Properties properties = new Properties();
        if (isXML) {
            properties.loadFromXML(input);
        } else {
            properties.load(input);
        }
        return properties;
    }

}
