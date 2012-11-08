package org.nnsoft.guice.rocoto.configuration;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple iterator of a {@code Map<K, V>} entries, with the option of prefixing keys with the given prefix.<br>
 * The iterator will iterate from its underlying map in a thread safe way.
 */
final class PropertiesIterator<K, V> implements Iterator<Entry<String, String>>
{

	/**
	 * Creates a new iterator over a map configuration with prefixing the keys with the given prefix.
	 * 
	 * @param <K> The map entry key type
	 * @param <V> The map entry value type
	 * @param properties The map configuration has to be read
	 * @return A map configuration iterator
	 */
	public static final <K, V> PropertiesIterator<K, V> newPropertiesIterator( Map<K, V> properties )
	{
		return new PropertiesIterator<K, V>(null, properties);
	}

	/**
	 * Creates a new iterator over a map configuration with prefixing the keys with the given prefix.
	 * 
	 * @param <K> the map entry key type.
	 * @param <V> the map entry value type.
	 * @param keyPrefix the prefix for key entries.
	 * @param properties the map configuration has to be read.
	 * @return a map configuration iterator.
	 */
	public static final <K, V> PropertiesIterator<K, V> newPropertiesIterator( String keyPrefix, Map<K, V> properties )
	{
		return new PropertiesIterator<K, V>(keyPrefix, properties);
	}

	/**
	 * The key prefix.
	 */
	private final String keyPrefix;

	/**
	 * The wrapped configuration iterator.
	 */
	private final Iterator<Entry<K, V>> propertiesIterator;

	/**
	 * Creates a new properties iterator.
	 * 
	 * @param keyPrefix the key prefix. It can be {@code null}.
	 * @param properties the wrapped configuration.
	 */
	private PropertiesIterator( String keyPrefix, Map<K, V> properties )
	{
		this.keyPrefix = keyPrefix;
		// Must synchronize on origin map as HashMap constructor may throws a concurrent exception while copying original entries
		synchronized (properties)
		{
			this.propertiesIterator = new HashMap<K, V>(properties).entrySet().iterator();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext()
	{
		return propertiesIterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	public Entry<String, String> next()
	{
		Entry<K, V> next = propertiesIterator.next();
		String key = String.valueOf(next.getKey());
		if ( keyPrefix != null && keyPrefix.length() > 0 )
		{
			key = keyPrefix + key;
		}
		return new KeyValue(key, String.valueOf(next.getValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove()
	{
		throw new UnsupportedOperationException("remove not supported on PropertiesIterator");
	}

}
