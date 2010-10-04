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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;
import com.googlecode.rocoto.configuration.resolver.PropertiesResolver;

/**
 * 
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public class ConfigurationModule extends AbstractModule {

    private final List<ConfigurationReader> readers = new ArrayList<ConfigurationReader>();

    public final ConfigurationModule addConfigurationReader(ConfigurationReader configurationReader) {
        this.readers.add(configurationReader);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void configure() {
        for (ConfigurationReader configurationReader : this.readers) {
            try {
                Iterator<Entry<String, String>> properties = configurationReader.readConfiguration();
                while (properties.hasNext()) {
                    Entry<String, String> property = properties.next();
                    LinkedBindingBuilder<String> bindingBuilder = this.bind(Key.get(String.class, Names.named(property.getKey())));

                    PropertiesResolver formatter = new PropertiesResolver(property.getValue());
                    if (formatter.containsKeys()) {
                        bindingBuilder.toProvider(formatter);
                    } else {
                        bindingBuilder.toInstance(property.getValue());
                    }
                }
            } catch (Exception e) {
                this.addError(e);
            }
        }
    }

}
