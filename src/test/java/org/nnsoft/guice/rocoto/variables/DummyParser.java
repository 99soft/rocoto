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
 * Dummy parser that reverse all provided input.
 */
public class DummyParser implements Parser
{
	public Resolver parse( final String input ) throws IllegalArgumentException
	{
		return new Resolver()
		{

			public String resolve( Map<String, String> data )
			{
				return new StringBuilder(input).reverse().toString();
			}

			public boolean needsResolving()
			{
				return true;
			}
		};
	}

}
