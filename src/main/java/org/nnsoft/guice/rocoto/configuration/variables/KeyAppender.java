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
package org.nnsoft.guice.rocoto.configuration.variables;

import java.util.Map;

/**
 * @since 6.0
 */
final class KeyAppender
    implements Appender
{

    private final String key;

    private final String defaultValue;

    public KeyAppender( final String key )
    {
        this( key, null );
    }

    public KeyAppender( final String key, final String defaultValue )
    {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public void append( StringBuilder buffer, Map<String, String> configuration )
    {
        String value = configuration.get( key );
        if ( value != null )
        {
            buffer.append( value );
        }
        else if ( defaultValue != null )
        {
            buffer.append( defaultValue );
        }
        else
        {
            buffer.append( "${" ).append( key ).append( '}' );
        }
    }

    @Override
    public String toString()
    {
        return "${" + key + "}";
    }

}
