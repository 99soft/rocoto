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
 * Appender which just wraps a text fragment to render as is.
 *
 * @since 6.0
 */
final class TextAppender extends AbstractAppender
{

    public TextAppender( final String chunk )
    {
        super( chunk );
    }

    @Override
    protected void doAppend( StringBuilder buffer, Map<String, String> configuration, Tree<Appender> context )
    {
        buffer.append( chunk );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }
        if ( obj instanceof TextAppender )
        {
            TextAppender other = (TextAppender) obj;
            return chunk != null ? chunk.equals( other.chunk ) : other.chunk == null;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return chunk != null ? chunk.hashCode() : 0;
    }

    /**
     * @return Always false
     */
    public boolean needsResolving()
    {
        return false;
    }

}
