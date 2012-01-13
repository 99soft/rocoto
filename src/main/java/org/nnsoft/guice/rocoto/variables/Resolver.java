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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 6.0
 */
final class Resolver
{

    private static final String VAR_BEGIN = "$";

    /**
     * The symbol that separates the key name to the default value.
     */
    private static final String PIPE_SEPARATOR = "|";

    private final List<Appender> appenders = new ArrayList<Appender>();

    private boolean containsKeys = false;

    public Resolver( final String pattern )
    {
        int prev = 0;
        int pos;
        while ( ( pos = pattern.indexOf( VAR_BEGIN, prev ) ) >= 0 )
        {
            if ( pos > 0 )
            {
                appenders.add( new TextAppender( pattern.substring( prev, pos ) ) );
            }
            if ( pos == pattern.length() - 1 )
            {
                appenders.add( new TextAppender( VAR_BEGIN ) );
                prev = pos + 1;
            }
            else if ( pattern.charAt( pos + 1 ) != '{' )
            {
                if ( pattern.charAt( pos + 1 ) == '$' )
                {
                    appenders.add( new TextAppender( VAR_BEGIN ) );
                    prev = pos + 2;
                }
                else
                {
                    appenders.add( new TextAppender( pattern.substring( pos, pos + 2 ) ) );
                    prev = pos + 2;
                }
            }
            else
            {
                int endName = pattern.indexOf( '}', pos );
                if ( endName < 0 )
                {
                    throw new IllegalArgumentException( "Syntax error in property: " + pattern );
                }
                final String key = pattern.substring( pos + 2, endName );

                int pipeIndex = key.indexOf( PIPE_SEPARATOR );
                if ( pipeIndex >= 0 )
                {
                    appenders.add( new KeyAppender( key.substring( 0, pipeIndex ).trim(),
                                                    key.substring( pipeIndex + 1 ).trim() ) );
                }
                else
                {
                    appenders.add( new KeyAppender( key ) );
                }

                prev = endName + 1;
                containsKeys = true;
            }
        }
        if ( prev < pattern.length() )
        {
            appenders.add( new TextAppender( pattern.substring( prev ) ) );
        }
    }

    public boolean containsKeys()
    {
        return containsKeys;
    }

    public String resolve( Map<String, String> configuration )
    {
        StringBuilder buffer = new StringBuilder();
        for ( Appender appender : appenders )
        {
            appender.append( buffer, configuration );
        }
        return buffer.toString();
    }

    @Override
    public String toString()
    {
        return appenders.toString();
    }

}
