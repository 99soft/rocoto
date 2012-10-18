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

import static java.lang.String.format;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Logger.getLogger;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Abstract Appender implementation handling resolving context management.
 *
 * @since 6.2
 */
abstract class AbstractAppender
    implements Appender
{

    /** Logger */
    private static final Logger logger = getLogger( AbstractAppender.class.getName() );

    /** Original chunk to process by this appender */
    protected final String chunk;

    /**
     * Default constructor
     *
     * @param chunk The chunk this appender has to process.
     */
    protected AbstractAppender( String chunk )
    {
        this.chunk = chunk;
    }

    /**
     * Move provided context to current appender and call {@link #doAppend(StringBuilder, Map, Tree, Parser)} if no recursion has been detected.
     *
     * @param buffer
     * @param configuration
     * @param context
     */
    public final void append( StringBuilder buffer, Map<String, String> configuration, Tree<Appender> context )
    {
        // Create context if needed
        Tree<Appender> currentContext = context == null ? new Tree<Appender>( this ) : context.addLeaf( this );

        // Check recursion
        if ( currentContext.inAncestors( this ) )
        {
            // For the moment just log a warning, and stop the resolving by appending original chunk
            buffer.append( chunk );

            logger.warning( format( "Recursion detected within variable resolving:%n%s", currentContext.getRoot() ) );
        }
        // Process real appending
        else
        {
            doAppend( buffer, configuration, currentContext );
            // Dump some info on resolution if this is a root appender
            if ( currentContext.isRoot() && logger.isLoggable( FINEST ) )
            {
                logger.finest( format( "Resolving variables:%n%s", currentContext ) );
            }
        }
    }

    /**
     * Begin resolving process with this appender against the provided configuration.
     */
    public String resolve( Map<String, String> configuration )
    {
        StringBuilder buffer = new StringBuilder();
        append( buffer, configuration, null );
        return buffer.toString();
    }

    /**
     * Append something to the provided buffer for the given configuration.<br>
     *
     * @param buffer
     * @param configuration
     * @param passed Resolving context, current element is the appender itself.
     */
    protected abstract void doAppend( StringBuilder buffer, Map<String, String> configuration, Tree<Appender> context );

    /**
     * Abstract to force subclasses to re-implement.
     */
    @Override
    public abstract boolean equals( Object obj );

    /**
     * Abstract to force subclasses to re-implement.
     */
    @Override
    public abstract int hashCode();

    /**
     * @return original chunk
     */
    @Override
    public final String toString()
    {
        return chunk;
    }

}
