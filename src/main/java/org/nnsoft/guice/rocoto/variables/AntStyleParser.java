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

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser implementation to resolve ant-style variables.
 *
 * <h2>Grammar</h2>
 *
 * <pre>
 * expression := (variable|text)*
 * variable := '${' expression '|' expression '}'
 * text := // any characters except '${'
 * </pre>
 *
 * <h2>Examples</h2>
 * <ul>
 * <li>Mixed expression: <code>${foo} and ${bar}</code></li>
 * <li>Variable with default value: <code>${foo|bar}</code>, <code>${foo|default value is ${bar}}</code>
 * <li>Dynamic variable: <code>${${foo.name}}</code></li>
 * <li>Etc. <code>${foo${bar|}|${other|${${fallback.name}}!}}</code></li>
 * </ul>
 *
 * <h3>Note</h3> The parser trim variable key and default value thus <tt>${ foo  | default     }</tt> is equals to <tt>${foo|default}</tt>.
 *
 * @since 6.2
 */
public class AntStyleParser
    implements Parser
{

    /** Grammar constants */
    static final String VAR_START = "${";

    static final int VAR_START_LEN = VAR_START.length();

    static final char VAR_CLOSE = '}';

    static final int VAR_CLOSE_LEN = 1;

    static final char PIPE_SEPARATOR = '|';

    static final int PIPE_SEPARATOR_LEN = 1;

    /**
     * FIXME: Refactor!
     */
    public Appender parse( String pattern )
    {
        List<Appender> appenders = new ArrayList<Appender>();
        int prev = 0;
        int pos = 0;
        while ( ( pos = pattern.indexOf( VAR_START, pos ) ) >= 0 )
        {
            // Add text between beginning/end of last variable
            if ( pos > prev )
            {
                appenders.add( new TextAppender( pattern.substring( prev, pos ) ) );
            }

            // Move to real variable name beginning
            pos += VAR_START_LEN;

            // Next close bracket (not necessarily the variable end bracket if
            // there is a default value with nested variables
            int endVariable = pattern.indexOf( VAR_CLOSE, pos );
            if ( endVariable < 0 )
            {
                throw new IllegalArgumentException( format( "Syntax error in property value '%s', missing close bracket '%s' for variable beginning at col %s: ''{3}''",
                                                            pattern, VAR_CLOSE, pos - VAR_START_LEN, pattern.substring( pos - VAR_START_LEN ) ) );
            }

            // Try to skip eventual internal variable here
            int nextVariable = pattern.indexOf( VAR_START, pos );
            // Just used to throw exception with more accurate message
            int lastEndVariable = endVariable;
            boolean hasNested = false;
            while ( nextVariable >= 0 && nextVariable < endVariable )
            {
                hasNested = true;
                endVariable = pattern.indexOf( VAR_CLOSE, endVariable + VAR_CLOSE_LEN );
                // Something is badly closed
                if ( endVariable < 0 )
                {
                    throw new IllegalArgumentException( format( "Syntax error in property value '%s', missing close bracket '%s' for variable beginning at col %s: '%s'",
                                                                pattern, VAR_CLOSE, nextVariable, pattern.substring( nextVariable, lastEndVariable ) ) );
                }
                nextVariable = pattern.indexOf( VAR_START, nextVariable + VAR_START_LEN );
                lastEndVariable = endVariable;
            }
            // The chunk to process
            final String rawKey = pattern.substring( pos - VAR_START_LEN, endVariable + VAR_CLOSE_LEN );
            // Key without variable start and end symbols
            final String key = pattern.substring( pos, endVariable );

            int pipeIndex = key.indexOf( PIPE_SEPARATOR );

            boolean hasKeyVariables = false;
            boolean hasDefault = false;
            boolean hasDefaultVariables = false;

            // There is a pipe
            if ( pipeIndex >= 0 )
            {
                // No nested property detected, simple default part
                if ( !hasNested )
                {
                    hasDefault = true;
                    hasDefaultVariables = false;
                }
                // There is a pipe and nested variable,
                // determine if pipe is for the current variable or a nested key
                // variable
                else
                {
                    int nextStartKeyVariable = key.indexOf( VAR_START );
                    hasKeyVariables = pipeIndex > nextStartKeyVariable;
                    if ( hasKeyVariables )
                    {
                        // ff${fdf}|${f}
                        int nextEndKeyVariable = key.indexOf( VAR_CLOSE, nextStartKeyVariable + VAR_START_LEN );
                        pipeIndex = key.indexOf( PIPE_SEPARATOR, pipeIndex + PIPE_SEPARATOR_LEN );
                        while ( pipeIndex >= 0 && pipeIndex > nextStartKeyVariable )
                        {
                            pipeIndex = key.indexOf( PIPE_SEPARATOR, nextEndKeyVariable + VAR_CLOSE_LEN );
                            nextStartKeyVariable = key.indexOf( VAR_START, nextStartKeyVariable + VAR_START_LEN );
                            // No more nested variable
                            if ( nextStartKeyVariable < 0 )
                            {
                                break;
                            }
                            nextEndKeyVariable = key.indexOf( VAR_CLOSE, nextEndKeyVariable + VAR_CLOSE_LEN );
                            if ( nextEndKeyVariable < 0 )
                            {
                                throw new IllegalArgumentException( format( "Syntax error in property value '%s', missing close bracket '%s' for variable beginning at col %s: '%s'",
                                                                            pattern, VAR_CLOSE, nextStartKeyVariable, key.substring( nextStartKeyVariable ) ) );
                            }
                        }
                    }

                    // nested variables are only for key, current variable does
                    // not have a default value
                    if ( pipeIndex >= 0 )
                    {
                        hasDefault = true;
                        hasDefaultVariables = key.indexOf( VAR_START, pipeIndex ) >= 0;
                    }

                }
            }
            // No pipe, there is key variables if nested elements have been
            // detected
            else
            {
                hasKeyVariables = hasNested;
            }

            // Construct variable appenders
            String keyPart = null;
            String defaultPart = null;
            if ( hasDefault )
            {
                keyPart = key.substring( 0, pipeIndex ).trim();
                defaultPart = key.substring( pipeIndex + PIPE_SEPARATOR_LEN ).trim();
            }
            else
            {
                keyPart = key.trim();
            }
            // Choose TextAppender when relevant to avoid unecessary parsing when it's clearly not needed
            appenders.add( new KeyAppender( this,
                                            rawKey,
                                            hasKeyVariables ? parse( keyPart ) : new TextAppender( keyPart ),
                                            !hasDefault ? null : ( hasDefaultVariables ? parse( defaultPart ) : new TextAppender( defaultPart ) ) ) );

            prev = endVariable + VAR_CLOSE_LEN;
            pos = prev;
        }

        if ( prev < pattern.length() )
        {
            appenders.add( new TextAppender( pattern.substring( prev ) ) );
        }

        return appenders.size() == 1 ? appenders.get( 0 ) : new MixinAppender( pattern, appenders );
    }

}
