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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract appender implementation.<br>
 * TODO: Move to a visitor pattern?
 */
abstract class AbstractAppender implements Appender
{
	/** Logger */
	private static final Logger logger = Logger.getLogger(AbstractAppender.class.getName());

	/**
	 * Move provided context to current appender and call
	 * {@link #doAppend(StringBuilder, Map, Tree)}.
	 */
	public final void append( StringBuilder buffer, Map<String, String> configuration, Tree<Appender> context )
	{
		Tree<Appender> currentContext = context == null ? new Tree<Appender>(this) : context.addLeaf(this);
		doAppend(buffer, configuration, currentContext);
		// Dump some info on resolution if this is a root appender
		if ( currentContext.isRoot() && logger.isLoggable(Level.FINEST) )
		{
			logger.finest(new StringBuilder("Resolving variables:\n").append(currentContext.toString()).toString());
		}
	}

	/** Do something */
	protected abstract void doAppend( StringBuilder buffer, Map<String, String> configuration, Tree<Appender> passed );

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
}
