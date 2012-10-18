package org.nnsoft.guice.rocoto.variables;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Composition appender which delegates the resolving to an inner list of appenders.
 */
final class MixinAppender
    extends AbstractAppender
{

    /** Inner appenders */
    private final List<Appender> appenders = new ArrayList<Appender>();

    /**
     * Constructor from array.
     *
     * @param chunk
     * @param appenders
     */
    public MixinAppender( String chunk, Appender... appenders )
    {
        this( chunk, Arrays.asList( appenders ) );
    }

    /**
     * Constructor from list.
     *
     * @param chunk
     * @param appenders
     */
    public MixinAppender( String chunk, List<Appender> appenders )
    {
        super( chunk );
        this.appenders.addAll( appenders );
    }

    @Override
    public void doAppend( StringBuilder buffer, Map<String, String> configuration, Tree<Appender> context )
    {
        for ( Appender appender : appenders )
        {
            appender.append( buffer, configuration, context );
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }
        if ( obj instanceof MixinAppender )
        {
            MixinAppender other = (MixinAppender) obj;
            if ( appenders.size() == other.appenders.size() )
            {
                return appenders.containsAll( other.appenders );
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return appenders.hashCode();
    }

    /**
     * @return True if at least one of the inner appenders need resolving
     */
    public boolean needsResolving()
    {
        for ( Appender appender : appenders )
        {
            if ( appender.needsResolving() )
            {
                return true;
            }
        }
        return false;
    }

}
