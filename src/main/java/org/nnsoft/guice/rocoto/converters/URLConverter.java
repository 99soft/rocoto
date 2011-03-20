/*
 *    Copyright 2009-2011 The 99 Software Foundation
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
package org.nnsoft.guice.rocoto.converters;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;

/**
 * Converter implementation for {@code java.net.URL}.
 */
public final class URLConverter extends AbstractConverter<URL> {

    /**
     * Pseudo URL prefix for loading from the class path: "classpath://"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath://";

    /**
     * {@inheritDoc}
     */
    public Object convert(String value, TypeLiteral<?> toType) {
        if (value.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = value.substring(CLASSPATH_URL_PREFIX.length());
            while ('/' == path.charAt(0)) {
                path = path.substring(1);
            }

            ClassLoader classLoader = null;
            try {
                classLoader = Thread.currentThread().getContextClassLoader();
            } catch (Throwable t) {
                // Cannot access thread context ClassLoader - falling back to system class loader...
            }
            if (classLoader == null) {
                // No thread context class loader -> use class loader of this class.
                classLoader = URLConverter.class.getClassLoader();
            }
            URL url = classLoader.getResource(path);
            if (url == null) {
                throw new ProvisionException("class path resource '"
                        + path
                        + "' cannot be resolved to URL because it does not exist");
            }

            return url;
        }

        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new ProvisionException("String value '"
                    + value
                    + "' is not a valid URL", e);
        }
    }

}
