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
package com.googlecode.rocoto.configuration;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Easy-to-use {@code Apache commons-configurations} wrapper,
 * built for users that require binding more complex configuration format.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class ConfigurationModule extends AbstractModule {

    private final Log log = LogFactory.getLog(this.getClass());

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final CompositeConfiguration configuration = new CompositeConfiguration();

    private final List<ConfigurationReader> readers = new ArrayList<ConfigurationReader>();

    /**
     * This class loader.
     */
    private final ClassLoader defaultClassLoader = this.getClass().getClassLoader();

    public ConfigurationModule addConfiguration(Configuration configuration) {
        this.configuration.addConfiguration(configuration);
        return this;
    }

    public ConfigurationModule loadConfiguration(Class<? extends FileConfiguration> configurationType, String classpathResource) {
        return this.loadConfiguration(configurationType, classpathResource, UTF_8);
    }

    public ConfigurationModule loadConfiguration(Class<? extends FileConfiguration> configurationType,
            String classpathResource,
            Charset encoding) {
        return this.loadConfiguration(configurationType, classpathResource, this.defaultClassLoader, encoding);
    }

    public ConfigurationModule loadConfiguration(Class<? extends FileConfiguration> configurationType,
            String classpathResource,
            ClassLoader classLoader) {
        return this.loadConfiguration(configurationType,
                classpathResource,
                classLoader,
                UTF_8);
    }

    public ConfigurationModule loadConfiguration(Class<? extends FileConfiguration> configurationType,
            String classpathResource,
            ClassLoader classLoader,
            Charset encoding) {
        this.readers.add(new ConfigurationReader(classpathResource, classLoader, configurationType, encoding));
        return this;
    }

    public ConfigurationModule loadConfiguration(Class<? extends FileConfiguration> configurationType,
            File configurationFile) {
        return this.loadConfiguration(configurationType, configurationFile, UTF_8);
    }

    public ConfigurationModule loadConfiguration(Class<? extends FileConfiguration> configurationType,
            File configurationFile,
            Charset encoding) {
        this.readers.add(new ConfigurationReader(configurationFile, configurationType, encoding));
        return this;
    }

    public ConfigurationModule loadConfiguration(Class<? extends FileConfiguration> configurationType, URL url, Charset encoding) {
        this.readers.add(new ConfigurationReader(url, configurationType, encoding));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
        for (ConfigurationReader reader : this.readers) {
            try {
                this.addConfiguration(reader.read());
            } catch (Exception e) {
                this.addError(e);
            }
        }

        Iterator<String> keys = this.configuration.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = this.configuration.getString(key);

            if (this.log.isDebugEnabled()) {
                this.log.debug("Binding property '"
                        + key
                        + " = "
                        + value);
            }

            this.bindConstant().annotatedWith(Names.named(key)).to(value);
        }
    }

}
