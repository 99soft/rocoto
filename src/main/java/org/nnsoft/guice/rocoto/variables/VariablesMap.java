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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @since 6.0
 */
public final class VariablesMap implements Map<String, String>
{
	/**
	 * Object representing a variable value
	 */
	private static class VariableValue implements Resolver
	{
		/** Original final value */
		private final String original;
		/** Current resolver for current parser */
		private final Resolver resolver;

		/**
		 * Default constructor
		 * 
		 * @param original
		 */
		private VariableValue( String original, Resolver resolver )
		{
			this.original = original;
			this.resolver = resolver;
		}

		@Override
		public String toString()
		{
			return format("VariableValue[original=''{0}'',resolver=''{2}'']", original, resolver);
		}

		public String getOriginal()
		{
			return original;
		}

		public String resolve( Map<String, String> data )
		{
			return resolver.resolve(data);
		}

		public boolean needsResolving()
		{
			return resolver.needsResolving();
		}
	}

	/** Parser to use for variables resolving */
	private Parser parser;

	public VariablesMap( Parser parser )
	{
		setParser(parser);
	}

	public VariablesMap()
	{
		this(new AntStyleParser());
	}

	private final Map<String, VariableValue> resolvers = new HashMap<String, VariableValue>();

	private final Map<String, String> snapshot = new HashMap<String, String>();

	public void clear()
	{
		resolvers.clear();
		snapshot.clear();
	}

	public boolean containsKey( Object key )
	{
		return snapshot.containsKey(key);
	}

	public boolean containsValue( Object value )
	{
		return snapshot.containsValue(value);
	}

	public Set<Entry<String, String>> entrySet()
	{
		return snapshot.entrySet();
	}

	public String get( Object key )
	{
		return snapshot.get(key);
	}

	public boolean isEmpty()
	{
		return snapshot.isEmpty();
	}

	public Set<String> keySet()
	{
		return snapshot.keySet();
	}

	public String put( String key, String value )
	{
		putValue(key, value);
		resolveVariables();
		return snapshot.get(key);
	}

	public void putAll( Map<? extends String, ? extends String> t )
	{
		for ( Entry<? extends String, ? extends String> entry : t.entrySet() )
		{
			putValue(entry.getKey(), entry.getValue());
		}
		resolveVariables();
	}

	public void putAll( Properties properties )
	{
		for ( Entry<Object, Object> entry : properties.entrySet() )
		{
			putValue(entry.getKey().toString(), entry.getValue().toString());
		}
		resolveVariables();
	}

	private void putValue( String key, String value )
	{
		snapshot.put(key, value);
		resolvers.put(key, new VariableValue(value, parser.parse(value)));
	}

	private void resolveVariables()
	{
		for ( Entry<String, VariableValue> entry : resolvers.entrySet() )
		{
			if ( entry.getValue().needsResolving() )
			{
				snapshot.put(entry.getKey(), entry.getValue().resolve(snapshot));
			}
		}
	}

	public String remove( Object key )
	{
		String value = null;
		if ( containsKey(key) )
		{
			value = snapshot.remove(key);
			resolvers.remove(key);
			resolveVariables();
		}
		return value;
	}

	public int size()
	{
		return snapshot.size();
	}

	public Collection<String> values()
	{
		return snapshot.values();
	}

	@Override
	public String toString()
	{
		return snapshot.toString();
	}

	public Parser getParser()
	{
		return parser;
	}

	public void setParser( Parser parser )
	{
		this.parser = parser == null ? new AntStyleParser() : parser;
		applyParser();
	}

	/**
	 * Re apply parser on all entries in map
	 */
	private void applyParser()
	{
		this.snapshot.clear();
		Map<String, String> originals = new HashMap<String, String>(this.resolvers.size());
		for ( Entry<String, VariableValue> resolver : this.resolvers.entrySet() )
		{
			originals.put(resolver.getKey(), resolver.getValue().getOriginal());
		}
		putAll(originals);
	}
}
