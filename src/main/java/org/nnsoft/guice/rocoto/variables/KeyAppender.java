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
import java.util.logging.Logger;

/**
 * @since 6.0
 */
final class KeyAppender extends AbstractAppender
{
	/** Logger */
	private static final Logger logger = Logger.getLogger(KeyAppender.class.getName());

	/**
	 * Appender which will resolve key (add the possibility for dynamic
	 * variable)
	 */
	private final Appender key;
	/** Appender which will resolve default value */
	private Appender defaultValue;

	/**
	 * Constructor for key without default value.
	 * 
	 * @param key
	 */
	public KeyAppender( final Appender key )
	{
		this(key, null);
	}

	/**
	 * Constructor for key with default value.
	 * 
	 * @param key
	 * @param defaultValue
	 *            nullable
	 */
	public KeyAppender( final Appender key, final Appender defaultValue )
	{
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
		key.append(keyBuffer, configuration, context);
		String resolvedKey = keyBuffer.toString();

		String resolvedValue = configuration.get(resolvedKey);
		if ( resolvedValue != null )
		{
			// Resolved value from the configuration may have variable
			// unresolved
			Resolver value = new Resolver(resolvedValue);
			if ( !value.containsKeys() )
			{
				buffer.append(resolvedValue);

			}
			// Processed appenders already contain a similar appender, there is
			// a recursion somewhere
			else if ( context.inAncestors(value) )
			{
				// For the moment just log a warning, and stop the resolving
				context.addLeaf(value);
				logger.warning(new StringBuilder("Recursion detected within variable resolving:\n").append(context.getRoot().toString()).toString());

				// Append resolved value with its variables unresolved
				buffer.append(resolvedValue);
			}
			// Process value
			else
			{
				StringBuilder resolvedValueBuffer = new StringBuilder();
				value.append(resolvedValueBuffer, configuration, context);
				resolvedValue = resolvedValueBuffer.toString();
				// Update the configuration
				configuration.put(resolvedKey, resolvedValue);
			}
		}
		// No value found from configuration, take default one
		else if ( defaultValue != null )
		{
			defaultValue.append(buffer, configuration, context);
		}
		// Fallback, print variable itself, will let the possibility to resolve
		// it later
		else
		{
			buffer.append("${").append(resolvedKey).append('}');
		}
	}

	@Override
	public String toString()
	{
		return "${" + key + (defaultValue != null ? "|" + defaultValue : "") + "}";
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
			return (key != null ? key.equals(other.key) : other.key == null)
					&& (defaultValue != null ? defaultValue.equals(other.defaultValue) : other.defaultValue == null);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return (key != null ? key.hashCode() : 0) + (defaultValue != null ? defaultValue.hashCode() * 31 : 0);
	}
}
