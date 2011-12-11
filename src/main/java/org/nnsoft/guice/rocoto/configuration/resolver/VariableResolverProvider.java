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
package org.nnsoft.guice.rocoto.configuration.resolver;

import static com.google.inject.name.Names.named;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * {@link Appender} implementation that resolve the ${} variables
 * and append the result to the given buffer; if the variable
 * won't be resolved, the default value, if any, will be used,
 * otherwise
 *
 * @since 4.0
 */
final class VariableResolverProvider
    implements Provider<String>
{

    /**
     * The key prefix, in its unresolved form.
     */
    private static final String KEY_PREFIX = "${";

    /**
     * The variable has to be resolved.
     */
    private final String variableName;

    /**
     * The default value used if the key won't be resolved.
     */
    private final String defaultValue;

    /**
     *
     */
    private Injector injector;

    /**
     * Creates a new KeyAppender with a property
     * key name and the default value.
     *
     * @param variableName the property variable name.
     * @param defaultValue the property default value.
     */
    public VariableResolverProvider( final String variableName, final String defaultValue )
    {
        this.variableName = variableName;
        this.defaultValue = defaultValue;
    }

    /**
     *
     *
     * @param injector
     */
    public void setInjector( Injector injector )
    {
        this.injector = injector;
    }

    /**
     * {@inheritDoc}
     */
    public String get()
    {
        try
        {
            return injector.getInstance( Key.get( String.class, named( variableName ) ) );
        }
        catch ( Throwable e )
        {
            if ( defaultValue != null )
            {
                return defaultValue;
            }
            return new StringBuilder().append( KEY_PREFIX ).append( variableName ).append( '}' ).toString();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder( KEY_PREFIX ).append( variableName );
        if ( defaultValue != null )
        {
            builder.append( '|' ).append( defaultValue );
        }
        builder.append( '}' );
        return builder.toString();
    }

}
