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
package com.rocoto.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.FileConfiguration;

import com.rocoto.core.AbstractURLReader;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
final class ConfigurationReader extends AbstractURLReader<Configuration> {

    private final Charset encoding;

    private final FileConfiguration fileConfiguration;

    public ConfigurationReader(File file,
            Class<? extends FileConfiguration> configurationType,
            Charset encoding) {
        super(file);
        this.encoding = encoding;
        this.fileConfiguration = create(configurationType);
    }

    public ConfigurationReader(String classpathResource,
            ClassLoader classLoader,
            Class<? extends FileConfiguration> configurationType,
            Charset encoding) {
        super(classpathResource, classLoader);
        this.encoding = encoding;
        this.fileConfiguration = create(configurationType);
    }

    public ConfigurationReader(URL url,
            Class<? extends FileConfiguration> configurationType,
            Charset encoding) {
        super(url);
        this.encoding = encoding;
        this.fileConfiguration = create(configurationType);
    }

    private static FileConfiguration create(Class<? extends FileConfiguration> configurationType) {
        try {
            return configurationType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating configuration on type '"
                    + configurationType.getName()
                    + "', please make sure the class has the empty default constructor");
        }
    }

    @Override
    protected Configuration process(InputStream input) throws Exception {
        Reader reader = null;
        try {
            reader = new InputStreamReader(input, this.encoding);
            this.fileConfiguration.load(reader);
            return this.fileConfiguration;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
