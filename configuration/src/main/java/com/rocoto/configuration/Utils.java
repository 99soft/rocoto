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
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
final class Utils {

    private Utils() {
    }

    public static URL toURL(String classpathResource, ClassLoader classLoader) {
        if (classpathResource == null) {
            throw new IllegalArgumentException("'classpathConfigurationUrl' argument can't be null");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("'classLoader' argument can't be null");
        }

        if ('/' == classpathResource.charAt(0)) {
            classpathResource = classpathResource.substring(1);
        }

        return classLoader.getResource(classpathResource);
    }

    public static URL toURL(File configurationFile) {
        if (configurationFile == null) {
            throw new IllegalArgumentException("'configurationFile' argument mustn't be null");
        }
        if (!configurationFile.exists()) {
            throw new IllegalArgumentException("Configuration file '"
                    + configurationFile.getAbsolutePath()
                    + "' doesn't exist");
        }
        if (configurationFile.isDirectory()) {
            throw new IllegalArgumentException("Impossible to load Configuration file '"
                    + configurationFile.getAbsolutePath()
                    + "' because it is a directory");
        }

        try {
            return configurationFile.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Impossible to load configuration file '"
                    + configurationFile.getAbsolutePath()
                    + ", see nested exceptions", e);
        }
    }

}
