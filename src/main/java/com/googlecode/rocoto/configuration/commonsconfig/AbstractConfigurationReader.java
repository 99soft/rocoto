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
package com.googlecode.rocoto.configuration.commonsconfig;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.configuration.FileConfiguration;

import com.googlecode.rocoto.configuration.internal.AbstractConfigurationURLReader;

/**
 * 
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
abstract class AbstractConfigurationReader<T extends FileConfiguration> extends AbstractConfigurationURLReader {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Charset charset;

    public AbstractConfigurationReader(File file) {
        this(file, UTF_8);
    }

    public AbstractConfigurationReader(File file, Charset charset) {
        super(file);
        this.charset = charset;
    }

    public AbstractConfigurationReader(String classpathResource) {
        this(classpathResource, UTF_8);
    }

    public AbstractConfigurationReader(String classpathResource, Charset charset) {
        this(classpathResource, Thread.currentThread().getContextClassLoader(), charset);
    }

    public AbstractConfigurationReader(String classpathResource, ClassLoader classLoader, Charset charset) {
        super(classpathResource, classLoader);
        this.charset = charset;
    }

    public AbstractConfigurationReader(URL url) {
        this(url, UTF_8);
    }

    public AbstractConfigurationReader(URL url, Charset charset) {
        super(url);
        this.charset = charset;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final Iterator<Entry<String, String>> process(InputStream input) throws Exception {
        Class<T> rawType = null;
        Type superclass = this.getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Type type = parameterized.getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            rawType = (Class<T>) actualTypeArguments[0];
        } else {
            throw new RuntimeException("Missing type parameter.");
        }

        Reader reader = new InputStreamReader(input, this.charset);

        try {
            T configuration = rawType.newInstance();
            configuration.load(reader);
            return new ConfigurationEntryIterator(configuration);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                // close quietly
            }
        }
    }

}
