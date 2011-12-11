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
package org.nnsoft.guice.rocoto.configuration;

import static com.google.inject.Key.get;
import static com.google.inject.internal.util.$Preconditions.checkNotNull;
import static com.google.inject.internal.util.$Preconditions.checkState;
import static com.google.inject.name.Names.named;
import static com.google.inject.util.Providers.guicify;
import static java.lang.String.format;
import static org.nnsoft.guice.rocoto.configuration.PropertiesIterator.newPropertiesIterator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.nnsoft.guice.rocoto.configuration.binder.PropertyValueBindingBuilder;
import org.nnsoft.guice.rocoto.configuration.binder.XMLPropertiesFormatBindingBuilder;
import org.nnsoft.guice.rocoto.configuration.resolver.PropertiesResolverProvider;

import com.google.inject.AbstractModule;
import com.google.inject.ProvisionException;
import com.google.inject.binder.LinkedBindingBuilder;

/**
 * The ConfigurationModule simplifies the task of loading configurations in Google Guice.
 */
public abstract class ConfigurationModule
    extends AbstractModule
{

    /**
     * The environment variable prefix, {@code env.}
     */
    private static final String ENV_PREFIX = "env.";

    /**
     * The {@code classpath} URL scheme constant
     */
    private static final String CLASSPATH_SCHEME = "classpath";

    private List<PropertiesURLReader> readers;

    @Override
    protected final void configure()
    {
        checkState( readers == null, "Re-entry not allowed" );

        readers = new LinkedList<PropertiesURLReader>();

        bindConfigurations();

        try
        {
            for ( PropertiesURLReader reader : readers )
            {
                try
                {
                    bindProperties( reader.readConfiguration() );
                }
                catch ( Exception e )
                {
                    addError( "An error occurred while reading properties from '%s': %s", reader.getUrl(),
                              e.getMessage() );
                }
            }
        }
        finally
        {
            readers = null;
        }
    }

    /**
     *
     */
    protected abstract void bindConfigurations();

    /**
     * Binds to a property with the given name.
     *
     * @param name The property name
     * @return The property value binder
     */
    protected PropertyValueBindingBuilder bindProperty( final String name )
    {
        checkNotNull( name, "Property name cannot be null." );

        return new PropertyValueBindingBuilder()
        {

            public void toValue( final String value )
            {
                checkNotNull( value, "Null value not admitted for property '%s's", name );

                LinkedBindingBuilder<String> builder = bind( get( String.class, named( name ) ) );

                PropertiesResolverProvider formatter = new PropertiesResolverProvider( value );
                if ( formatter.containsKeys() )
                {
                    builder.toProvider( guicify( formatter ) );
                }
                else
                {
                    builder.toInstance( value );
                }
            }

        };
    }

    /**
     *
     * @param properties
     * @return
     */
    protected void bindProperties( Properties properties )
    {
        checkNotNull( properties, "Parameter 'properties' must be not null" );

        bindProperties( newPropertiesIterator( properties ) );
    }

    /**
     *
     * @param properties
     */
    protected void bindProperties( Iterable<Entry<String, String>> properties )
    {
        checkNotNull( properties, "Parameter 'properties' must be not null" );

        bindProperties( properties.iterator() );
    }

    /**
     *
     * @param properties
     */
    protected void bindProperties( Iterator<Entry<String, String>> properties )
    {
        checkNotNull( properties, "Parameter 'properties' must be not null" );

        while ( properties.hasNext() )
        {
            Entry<String, String> property = properties.next();
            bindProperty( property.getKey() ).toValue( property.getValue() );
        }
    }

    /**
     * Add the Environment Variables properties, prefixed by {@code env.}.
     */
    protected void bindSystemProperties()
    {
        bindProperties( System.getProperties() );
    }

    /**
     *
     * @param properties
     * @return
     */
    protected void bindProperties( Map<String, String> properties )
    {
        checkNotNull( properties, "Parameter 'properties' must be not null" );

        bindProperties( newPropertiesIterator( properties ) );
    }

    /**
     * Add the System Variables properties.
     */
    protected void bindEnvironmentVariables()
    {
        bindProperties( newPropertiesIterator( ENV_PREFIX, System.getenv() ) );
    }

    /**
     *
     *
     * @param propertiesResource
     * @return
     */
    protected XMLPropertiesFormatBindingBuilder bindProperties( final File propertiesResource )
    {
        checkNotNull( propertiesResource, "Parameter 'propertiesResource' must be not null" );

        return bindProperties( propertiesResource.toURI() );
    }

    /**
     *
     *
     * @param propertiesResource
     * @return
     */
    protected XMLPropertiesFormatBindingBuilder bindProperties( final URI propertiesResource )
    {
        checkNotNull( propertiesResource, "Parameter 'propertiesResource' must be not null" );

        if ( CLASSPATH_SCHEME.equals( propertiesResource.getScheme() ) )
        {
            String path = propertiesResource.getPath();
            if ( propertiesResource.getHost() != null )
            {
                path = propertiesResource.getHost() + path;
            }
            return bindProperties( path );
        }

        try
        {
            return bindProperties( propertiesResource.toURL() );
        }
        catch ( MalformedURLException e )
        {
            throw new ProvisionException( format( "URI '%s' not supported: %s", propertiesResource, e.getMessage() ) );
        }
    }

    /**
     *
     * @param classPathResource
     * @return
     */
    protected XMLPropertiesFormatBindingBuilder bindProperties( final String classPathResource )
    {
        return bindProperties( classPathResource, getClass().getClassLoader() );
    }

    /**
     *
     * @param classPathResource
     * @param classLoader
     * @return
     */
    protected XMLPropertiesFormatBindingBuilder bindProperties( final String classPathResource,
                                                                final ClassLoader classLoader )
    {
        checkNotNull( classPathResource, "Parameter 'classPathResource' must be not null" );
        checkNotNull( classLoader, "Parameter 'classLoader' must be not null" );

        String resourceURL = classPathResource;
        if ( '/' == classPathResource.charAt( 0 ) )
        {
            resourceURL = classPathResource.substring( 1 );
        }

        URL url = classLoader.getResource( resourceURL );
        checkNotNull( url,
                      "ClassPath resource '%s' not found, make sure it is in the ClassPath or you're using the right ClassLoader",
                      classPathResource );

        return bindProperties( url );
    }

    /**
     *
     * @param propertiesResource
     * @return
     */
    protected XMLPropertiesFormatBindingBuilder bindProperties( final URL propertiesResource )
    {
        checkNotNull( propertiesResource, "parameter 'propertiesResource' must not be null" );

        PropertiesURLReader reader = new PropertiesURLReader( propertiesResource );
        readers.add( reader );
        return reader;
    }

}
