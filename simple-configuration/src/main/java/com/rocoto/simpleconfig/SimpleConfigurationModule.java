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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class SimpleConfigurationModule extends AbstractModule {

    private static final String DEFAULT_ENV_PREFIX = "env.";

    private static final Pattern VARIABLE_REPLACE_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    /**
     * This class logger.
     */
    private final Log log = LogFactory.getLog(this.getClass());

    /**
     * The stored load configurations.
     */
    private final Properties configuration = new Properties();

    /**
     * This class loader.
     */
    private final ClassLoader defaultClassLoader = this.getClass().getClassLoader();

    /**
     * 
     */
    private final AbstractPropertiesFileFilter defaultFileFilter = new DefaultPropertiesFileFilter();

    /**
     * Adds a {@link Properties} to the Guice Binder by loading the properties
     * file in the classpath.
     *
     * @param classpathConfigurationUrl
     */
    public void addProperties(String classpathConfigurationUrl) {
        this.addProperties(classpathConfigurationUrl, this.defaultClassLoader);
    }

    public void addProperties(String classpathConfigurationUrl, ClassLoader classLoader) {
        this.addProperties(classpathConfigurationUrl, classLoader, false);
    }

    public void addXMLProperties(String classpathConfigurationUrl) {
        this.addXMLProperties(classpathConfigurationUrl, this.defaultClassLoader);
    }

    public void addXMLProperties(String classpathConfigurationUrl, ClassLoader classLoader) {
        this.addProperties(classpathConfigurationUrl, this.defaultClassLoader, true);
    }

    private void addProperties(String classpathConfigurationUrl, ClassLoader classLoader, boolean isXML) {
        if (classpathConfigurationUrl == null) {
            throw new IllegalArgumentException("'classpathConfigurationUrl' argument can't be null");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("'classLoader' argument can't be null");
        }

        if ('/' == classpathConfigurationUrl.charAt(0)) {
            classpathConfigurationUrl = classpathConfigurationUrl.substring(1);
        }

        if (this.log.isDebugEnabled()) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Loading ");
            if (isXML) {
                messageBuilder.append("XML");
            }
            messageBuilder.append(" classpath resource '");
            messageBuilder.append(classpathConfigurationUrl);
            messageBuilder.append("' using class loader '");
            messageBuilder.append(classLoader.getClass().getName());
            messageBuilder.append("'");

            this.log.debug(messageBuilder);
        }

        this.addProperties(classLoader.getResource(classpathConfigurationUrl), isXML);
    }

    public void addProperties(File configurationFile) {
        this.addProperties(configurationFile, this.defaultFileFilter);
    }

    public void addProperties(File configurationFile, AbstractPropertiesFileFilter filter) {
        if (configurationFile == null) {
            throw new IllegalArgumentException("'configurationFile' argument can't be null");
        }
        if (configurationFile == null) {
            throw new IllegalArgumentException("'filter' argument can't be null");
        }

        if (!configurationFile.exists()) {
            throw new RuntimeException("Impossible to load properties file '"
                    + configurationFile
                    + " because it doesn't exist");
        }

        if (configurationFile.isDirectory()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Configuration file '"
                    + configurationFile.getAbsolutePath()
                    + "' is a directory, traversing it to look for properties file");
            }
            File[] childs = configurationFile.listFiles(filter);
            if (childs == null || childs.length == 0) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Configuration directory file '"
                            + configurationFile.getAbsolutePath()
                            + "' is empty");
                }
                return;
            }
            for (File file : childs) {
                this.addProperties(file, filter);
            }
            return;
        }

        try {
            this.addProperties(configurationFile.toURL(), filter.isXMLProperties(configurationFile));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Impossible to load properties file '"
                    + configurationFile.getAbsolutePath()
                    + ", see nested exceptions", e);
        }
    }

    public void addProperties(URL configurationUrl) {
        this.addProperties(configurationUrl, false);
    }

    public void addXMLProperties(URL configurationUrl) {
        this.addProperties(configurationUrl, true);
    }

    private void addProperties(URL configurationUrl, boolean isXML) {
        if (configurationUrl == null) {
            throw new IllegalArgumentException("'configurationUrl' argument can't be null");
        }

        if (this.log.isDebugEnabled()) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Loading ");
            if (isXML) {
                messageBuilder.append("XML");
            }
            messageBuilder.append(" configurationUrl '");
            messageBuilder.append(configurationUrl);
            messageBuilder.append("'");

            this.log.debug(messageBuilder);
        }

        URLConnection connection = null;
        InputStream input = null;
        try {
            connection = configurationUrl.openConnection();
            input = connection.getInputStream();

            if (isXML) {
                this.configuration.loadFromXML(input);
            } else {
                this.configuration.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible to open configuration URL "
                    + configurationUrl
                    + ", see nested exceptions", e);
        } finally {
            if (connection != null && (connection instanceof HttpURLConnection)) {
                ((HttpURLConnection) connection).disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void addSystemProperties() {
        this.addProperties(System.getProperties());
    }

    public void addEnvironmentVariables() {
        this.addEnvironmentVariables(DEFAULT_ENV_PREFIX);
    }

    public void addEnvironmentVariables(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            throw new IllegalArgumentException("empty prefix not allowed");
        }

        if (prefix.charAt(prefix.length() - 1) != '.') {
            prefix += '.';
        }

        for (Entry<String, String> envVar : System.getenv().entrySet()) {
            this.configuration.setProperty(prefix + envVar.getKey(), envVar.getValue());
        }
    }

    public void addProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("'properties' argument can't be null");
        }
        this.configuration.putAll(properties);
    }

    @Override
    protected void configure() {
        String key;
        String value;
        String variable;
        String variableKey;
        String variableValue;
        StringBuffer buffer;

        for (Entry<Object, Object> entry : this.configuration.entrySet()) {
            key = entry.getKey().toString();
            value = entry.getValue().toString();

            Matcher matcher = VARIABLE_REPLACE_PATTERN.matcher(value);
            buffer = new StringBuffer();
            while (matcher.find()) {
                variable = matcher.group();
                variableKey = matcher.group(1);
                variableValue = this.configuration.getProperty(variableKey);

                if (variableValue != null) {
                    matcher.appendReplacement(buffer, variableValue);
                } else {
                    buffer.append(variable);
                }
            }
            matcher.appendTail(buffer);

            this.bindConstant().annotatedWith(Names.named(key)).to(buffer.toString());
        }
    }

}
