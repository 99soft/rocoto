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
package com.googlecode.rocoto.configuration.traversal;

import java.io.File;

import org.nnsoft.guice.rocoto.configuration.ConfigurationReader;

import com.googlecode.rocoto.configuration.readers.PropertiesURLReader;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class XMLPropertiesReaderBuilder extends ConfigurationReaderBuilder {

    private static final String XML_PROPERTIES_PATTERN = "**/*.xml";

    public XMLPropertiesReaderBuilder() {
        super(XML_PROPERTIES_PATTERN);
    }

    @Override
    public ConfigurationReader create(File configurationFile) {
        return new PropertiesURLReader(configurationFile, true);
    }

}
