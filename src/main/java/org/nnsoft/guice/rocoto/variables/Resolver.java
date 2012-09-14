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

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 6.0
 */
final class Resolver extends AbstractAppender
{

	private static final char VAR_BEGIN = '$';
	private static final char VAR_OPEN = '{';
	private static final char VAR_CLOSE = '}';

	/** ${ */
	private static final String VAR_START = String.valueOf(new char[] { VAR_BEGIN, VAR_OPEN });

	/**
	 * The symbol that separates the key name to the default value.
	 */
	private static final char PIPE_SEPARATOR = '|';

	private final List<Appender> appenders = new ArrayList<Appender>();

	private boolean containsKeys = false;

	public Resolver( final String pattern )
	{

		int prev = 0;
		int pos = 0;
		while ((pos = pattern.indexOf(VAR_START, pos)) >= 0)
		{
			// Add text between beginning/end of last variable
			if ( pos > prev )
			{
				appenders.add(new TextAppender(pattern.substring(prev, pos)));
			}

			// Move to real variable name beginning
			pos += 2;

			// Next close bracket (not necessarily the variable end bracket if
			// there is a default value with nested variables
			int endVariable = pattern.indexOf(VAR_CLOSE, pos);
			if ( endVariable < 0 )
			{
				throw new IllegalArgumentException(format(
						"Syntax error in property value ''{0}'', missing close bracket ''{1}'' for variable beginning at col {2}: ''{3}''", pattern,
						VAR_CLOSE, pos - 2, pattern.substring(pos - 2)));
			}

			// Try to skip eventual internal variable here
			int nextVariable = pattern.indexOf(VAR_START, pos);
			// Just used to throw exception with more accurate message
			int lastEndVariable = endVariable;
			boolean hasNested = false;
			while (nextVariable >= 0 && nextVariable < endVariable)
			{
				hasNested = true;
				endVariable = pattern.indexOf(VAR_CLOSE, endVariable + 1);
				// Something is badly closed
				if ( endVariable < 0 )
				{
					throw new IllegalArgumentException(format(
							"Syntax error in property value ''{0}'', missing close bracket ''{1}'' for variable beginning at col {2}: ''{3}''",
							pattern, VAR_CLOSE, nextVariable, pattern.substring(nextVariable, lastEndVariable)));
				}
				nextVariable = pattern.indexOf(VAR_START, nextVariable + 2);
				lastEndVariable = endVariable;
			}

			final String key = pattern.substring(pos, endVariable);

			int pipeIndex = key.indexOf(PIPE_SEPARATOR);

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
					int nextStartKeyVariable = key.indexOf(VAR_START);
					hasKeyVariables = pipeIndex > nextStartKeyVariable;
					if ( hasKeyVariables )
					{
						// ff${fdf}|${f}
						int nextEndKeyVariable = key.indexOf(VAR_CLOSE, nextStartKeyVariable + 2);
						pipeIndex = key.indexOf(PIPE_SEPARATOR, pipeIndex + 1);
						while (pipeIndex >= 0 && pipeIndex > nextStartKeyVariable)
						{
							pipeIndex = key.indexOf(PIPE_SEPARATOR, nextEndKeyVariable + 1);
							nextStartKeyVariable = key.indexOf(VAR_START, nextStartKeyVariable + 2);
							// No more nested variable
							if ( nextStartKeyVariable < 0 )
							{
								break;
							}
							nextEndKeyVariable = key.indexOf(VAR_CLOSE, nextEndKeyVariable + 1);
							if ( nextEndKeyVariable < 0 )
							{
								throw new IllegalArgumentException(
										format("Syntax error in property value ''{0}'', missing close bracket ''{1}'' for variable beginning at col {2}: ''{3}''",
												pattern, VAR_CLOSE, nextStartKeyVariable, key.substring(nextStartKeyVariable)));
							}
						}
					}

					// nested variables are only for key, current variable does
					// not have a default value
					if ( pipeIndex >= 0 )
					{
						hasDefault = true;
						hasDefaultVariables = key.indexOf(VAR_BEGIN, pipeIndex) >= 0;
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
				keyPart = key.substring(0, pipeIndex).trim();
				defaultPart = key.substring(pipeIndex + 1).trim();
			} else
			{
				keyPart = key.trim();
			}
			// Choose TextAppender when relevant to avoid unecessary parsing
			// when not needed
			appenders.add(new KeyAppender(hasKeyVariables ? new Resolver(keyPart) : new TextAppender(keyPart), !hasDefault ? null
					: (hasDefaultVariables ? new Resolver(defaultPart) : new TextAppender(defaultPart))));

			prev = endVariable + 1;
			pos = prev;
			containsKeys = true;
		}

		if ( prev < pattern.length() )
		{
			appenders.add(new TextAppender(pattern.substring(prev)));
		}
	}

	public boolean containsKeys()
	{
		return containsKeys;
	}

	/**
	 * Resolve the value against a given configuration.
	 * 
	 * @param configuration
	 * @return
	 */
	public String resolve( Map<String, String> configuration )
	{
		StringBuilder buffer = new StringBuilder();
		append(buffer, configuration, null);
		return buffer.toString();
	}

	@Override
	public void doAppend( StringBuilder buffer, Map<String, String> configuration, Tree<Appender> context )
	{
		for ( Appender appender : appenders )
		{
			appender.append(buffer, configuration, context);
		}
	}

	@Override
	public String toString()
	{
		return appenders.toString();
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( obj == this )
		{
			return true;
		}
		if ( obj instanceof Resolver )
		{
			Resolver other = (Resolver) obj;
			if ( appenders.size() == other.appenders.size() )
			{
				return appenders.containsAll(other.appenders);
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return appenders.hashCode();
	}
}
