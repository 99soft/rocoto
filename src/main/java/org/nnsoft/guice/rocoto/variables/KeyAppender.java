/*
 *    Copyright 2009-2012 The 99 Software Foundation
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
package org.nnsoft.guice.rocoto.variables;

import java.util.Map;

/**
 * Appender which relies on another appender to provides a configuration key, and a fallback appender in case no
 * configuration value is found.
 *
 * @since 6.0
 */
final class KeyAppender
    extends AbstractAppender
{

    /** Appender which will resolve key (add the possibility for dynamic variable) */
    private final Appender key;

    /** Appender which will resolve default value */
    private Appender defaultValue;

    /** Parser to use if dynamic resolution is needed */
    private Parser parser;

    /**
     * Constructor for key without default value.
     *
     * @param parser The parser from which this appender has been created.
     * @param chunk
     * @param key Appender to resolve configuration key.
     */
    public KeyAppender( final Parser parser, final String chunk, final Appender key )
    {
        this( parser, chunk, key, null );
    }

    /**
     * Constructor for key without default value.
     *
     * @param parser The parser from which this appender has been created.
     * @param chunk
     * @param key Appender to resolve configuration key.
     * @param defaultValue Appender to resolve default value, may be null.
     */
    public KeyAppender( final Parser parser, final String chunk, final Appender key, final Appender defaultValue )
    {
        super( chunk );
        this.parser = parser;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doAppend( StringBuilder buffer, Map<String, String> configuration, Tree<Appender> context )
    {
        // Resolve key eventually
        StringBuilder keyBuffer = new StringBuilder();
        key.append( keyBuffer, configuration, context );
        String resolvedKey = keyBuffer.toString();

        String resolvedValue = configuration.get( resolvedKey );
        if ( resolvedValue != null )
        {
            // Resolved value from the configuration may have variable
            // unresolved
            Resolver value = parser.parse( resolvedValue );
            if ( !value.needsResolving() )
            {
                buffer.append( resolvedValue );

            }
            // Process value
            else
            {
                if ( !( value instanceof Appender ) )
                {
                    resolvedValue = value.resolve( configuration );
                }
                else
                {
                    StringBuilder resolvedValueBuffer = new StringBuilder();
                    ( (Appender) value ).append( resolvedValueBuffer, configuration, context );
                    resolvedValue = resolvedValueBuffer.toString();
                }

                buffer.append( resolvedValue );
                // Update the configuration
                configuration.put( resolvedKey, resolvedValue );
            }
        }
        // No value found from configuration, take default one
        else if ( defaultValue != null )
        {
            defaultValue.append( buffer, configuration, context );
        }
        // Fallback, print original chunk, will let the possibility to resolve
        // it later
        else
        {
            buffer.append( chunk );
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }
        if ( obj instanceof KeyAppender )
        {
            KeyAppender other = (KeyAppender) obj;
            return ( key != null ? key.equals( other.key ) : other.key == null )
                && ( defaultValue != null ? defaultValue.equals( other.defaultValue ) : other.defaultValue == null );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return ( key != null ? key.hashCode() : 0 ) + ( defaultValue != null ? defaultValue.hashCode() * 31 : 0 );
    }

    /**
     * @return Always true
     */
    public boolean needsResolving()
    {
        return true;
    }

}
