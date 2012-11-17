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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test variable resolving with {@link AntStyleParser}: default value, variables in default value, dynamic variable, recursive variable, etc.
 * 
 */
public class VariableResolvingTestCase
{
	private VariablesMap variablesMap;

	/**
	 * Set up some variables use cases to test on
	 */
	@Before
	public void setUp()
	{
		variablesMap = new VariablesMap(new AntStyleParser());

		variablesMap.put("prop.1", "One");
		variablesMap.put("prop.2", "Two");
		variablesMap.put("prop.3", "Three");
		variablesMap.put("found", "I'm here");
		variablesMap.put("real", "delegated value");
		variablesMap.put("dollarSymbol", "$");
		variablesMap.put("dollarGod", "$$$ Prey the $ god! ${dollarSymbol}${dollarSymbol}${dollarSymbol}");
		variablesMap.put("Three", "yeah");

		variablesMap.put("simple", "${prop.1}, ${prop.2}, ${prop.3}");
		variablesMap.put("delegate", "${real|fallback value}");
		variablesMap.put("withDefault", "${not.found|default value}");
		variablesMap.put("withEmptyDefault", "${not.found|}");

		variablesMap.put("withVariableDefault", "${not.found|${found}}");
		variablesMap.put("withDelegatedVariableDefault", "${not.found|${delegate}}");
		variablesMap.put("withMixinDefault", "${not.found|${found}, and i'm hungry}");
		variablesMap.put("withMixinDefault2", "${not.found|${prop.1} moment please, ok ${found}}");
		variablesMap.put("withDefaultOfDefault", "${not.found|${not.found.again|Crap!}}");
		variablesMap.put("withLotsOfDefault", "${not.found|${not.found.again|${found} to ${last.hope|${oh.really|kill}} it with fire!}}");

		variablesMap.put("dynamicSimpleVariable", "${${prop.1}}");
		variablesMap.put("dynamicDefaultVariable", "Property: ${prop.${number|1}}!");
		variablesMap.put("dynamicDefaultVariableWithDefault", "${${prop.4|${prop.1}}|fallback}");
		variablesMap.put("dynamicDefaultVariableWithDefaultDynamicVariable", "${${prop.4|${prop.1}}|${${fallback|${prop.3}}}}");

		variablesMap.put("hello.en", "Hi!");
		variablesMap.put("hello.fr", "Salut !");
		variablesMap.put("hello.i18n", "${hello.${locale|en}}");

		variablesMap.put("trimKey", "${prop.1}");
		variablesMap.put("notrimKey", "${     prop.1	 }");
		variablesMap.put("trimDefault", "${not.found|default}");
		variablesMap.put("notrimDefault", "${not.found|    default	  }");
		variablesMap.put("trimDynamic", "${${prop.3}}");
		variablesMap.put("notrimDynamic", "${   ${   prop.3		}	}");

	}

	/**
	 * Test variable replacement.
	 */
	@Test
	public void verifySimpleVariables()
	{
		assertEquals("One, Two, Three", variablesMap.get("simple"));
	}

	/**
	 * Test variables transitivity.
	 */
	@Test
	public void verifyDelegateVariables()
	{
		assertEquals("delegated value", variablesMap.get("delegate"));
	}

	/**
	 * Test variables with simple default value.
	 */
	@Test
	public void verifyVariablesWithDefault()
	{
		assertNull(variablesMap.get("not.found"));
		assertEquals("default value", variablesMap.get("withDefault"));
		variablesMap.put("not.found", "Surprise!");
		assertEquals("Surprise!", variablesMap.get("withDefault"));
	}

	/**
	 * Test variables with empty default value.
	 */
	@Test
	public void verifyVariablesWithEmptyDefault()
	{
		assertNull(variablesMap.get("not.found"));
		assertEquals("", variablesMap.get("withEmptyDefault"));
		variablesMap.put("not.found", "Surprise!");
		assertEquals("Surprise!", variablesMap.get("withEmptyDefault"));
	}

	/**
	 * Test variables with variable as default value.
	 */
	@Test
	public void verifyVariablesWithVariableAsDefault()
	{
		assertNull(variablesMap.get("not.found"));
		assertEquals("I'm here", variablesMap.get("withVariableDefault"));
		variablesMap.put("not.found", "Surprise!");
		assertEquals("Surprise!", variablesMap.get("withVariableDefault"));
	}

	/**
	 * Test variables transitivity on default variable.
	 */
	@Test
	public void verifyVariablesWithDelegatedVariableAsDefault()
	{
		assertNull(variablesMap.get("not.found"));
		assertEquals("delegated value", variablesMap.get("withDelegatedVariableDefault"));
		variablesMap.put("not.found", "Surprise!");
		assertEquals("Surprise!", variablesMap.get("withDelegatedVariableDefault"));
	}

	/**
	 * Test variables with mixin default value.
	 */
	@Test
	public void verifyVariablesWithMixinDefault()
	{
		assertNull(variablesMap.get("not.found"));
		assertEquals("I'm here, and i'm hungry", variablesMap.get("withMixinDefault"));
	}

	/**
	 * Test variables with mixin default value.
	 */
	@Test
	public void verifyVariablesWithMixinDefault2()
	{
		assertNull(variablesMap.get("not.found"));
		assertEquals("One moment please, ok I'm here", variablesMap.get("withMixinDefault2"));
	}

	/**
	 * Test variables with nested default value.
	 */
	@Test
	public void verifyVariablesWithDefaultOfDefault()
	{
		assertNull(variablesMap.get("not.found"));
		assertNull(variablesMap.get("not.found.again"));
		assertEquals("Crap!", variablesMap.get("withDefaultOfDefault"));
		variablesMap.put("not.found.again", "Lesser crap!");
		assertEquals("Lesser crap!", variablesMap.get("withDefaultOfDefault"));
		variablesMap.put("not.found", "At least!");
		assertEquals("At least!", variablesMap.get("withDefaultOfDefault"));
	}

	/**
	 * Test variables with complex mixin nested default value.
	 */
	@Test
	public void verifyVariablesWithNestedDefaults()
	{
		assertNull(variablesMap.get("not.found"));
		assertNull(variablesMap.get("not.found.again"));
		assertNull(variablesMap.get("last.hope"));
		assertNull(variablesMap.get("oh.really"));

		assertEquals("I'm here to kill it with fire!", variablesMap.get("withLotsOfDefault"));
		variablesMap.put("oh.really", "hurt");
		assertEquals("I'm here to hurt it with fire!", variablesMap.get("withLotsOfDefault"));
		variablesMap.put("last.hope", "tease");
		assertEquals("I'm here to tease it with fire!", variablesMap.get("withLotsOfDefault"));
		variablesMap.put("not.found.again", "fire any${prop.1}?");
		assertEquals("fire anyOne?", variablesMap.get("withLotsOfDefault"));
		variablesMap.put("not.found", "don't care...");
		assertEquals("don't care...", variablesMap.get("withLotsOfDefault"));
	}

	/**
	 * Test simple dynamic variables
	 */
	@Test
	public void verifySimpleDynamicVariables()
	{
		assertNull(variablesMap.get("One"));
		variablesMap.put("One", "like PHP!");
		assertEquals("like PHP!", variablesMap.get("dynamicSimpleVariable"));
	}

	/**
	 * Test variables which name is based on other variables with default.
	 */
	@Test
	public void verifyDynamicDefaultVariables()
	{
		assertNull(variablesMap.get("number"));
		assertEquals("Property: One!", variablesMap.get("dynamicDefaultVariable"));
		variablesMap.put("number", "2");
		assertEquals("Property: Two!", variablesMap.get("dynamicDefaultVariable"));
		variablesMap.put("number", "3");
		assertEquals("Property: Three!", variablesMap.get("dynamicDefaultVariable"));
		variablesMap.put("number", "${not.found|2}");
		assertNull(variablesMap.get("not.found"));
		assertEquals("Property: Two!", variablesMap.get("dynamicDefaultVariable"));
	}

	/**
	 * Test variables which name is based on other variables with default.
	 */
	@Test
	public void verifyDynamicDefaultVariables2()
	{
		assertNull(variablesMap.get("locale"));
		assertEquals("Hi!", variablesMap.get("hello.i18n"));
		variablesMap.put("locale", "fr");
		assertEquals("Salut !", variablesMap.get("hello.i18n"));
	}

	/**
	 * Test dynamic default variables with default value
	 */
	@Test
	public void verifyDynamicDefaultVariablesWithDefault()
	{
		assertNull(variablesMap.get("prop.4"));
		assertNull(variablesMap.get("One"));

		assertEquals("fallback", variablesMap.get("dynamicDefaultVariableWithDefault"));
		variablesMap.put("One", "${prop.2}");
		assertEquals("Two", variablesMap.get("dynamicDefaultVariableWithDefault"));
		variablesMap.put("prop.4", "prop.1");
		assertEquals("One", variablesMap.get("dynamicDefaultVariableWithDefault"));
	}

	/**
	 * Test dynamic default variables with default dynamic variable
	 */
	@Test
	public void verifyDynamicDefaultVariableWithDefaultDynamicVariable()
	{
		assertNull(variablesMap.get("prop.4"));
		assertNull(variablesMap.get("One"));
		assertNull(variablesMap.get("fallback"));

		assertEquals("yeah", variablesMap.get("dynamicDefaultVariableWithDefaultDynamicVariable"));
		variablesMap.put("fallback", "prop.3");
		assertEquals("Three", variablesMap.get("dynamicDefaultVariableWithDefaultDynamicVariable"));
		variablesMap.put("One", "${prop.2}");
		assertEquals("Two", variablesMap.get("dynamicDefaultVariableWithDefaultDynamicVariable"));
		variablesMap.put("prop.4", "prop.1");
		assertEquals("One", variablesMap.get("dynamicDefaultVariableWithDefaultDynamicVariable"));
	}

	@Test
	public void verifyGreatAnswer()
	{
		Map<String, String> t = new HashMap<String, String>();
		t.put("g", "2g");
		t.put("gg2", "2");
		t.put("h", "${${${g}4|${g}2}}");
		t.put("2g", "gg");
		t.put("2g2", "4");
		t.put("gg", "${h}${4|${${2g}2}}");
		variablesMap.clear();
		variablesMap.putAll(t);
		assertEquals("42", variablesMap.get("gg"));
		assertEquals(6, variablesMap.size());
	}

	/**
	 * Check no infinite loop on direct recursion.<br>
	 * Check recursive variable grow each time variables are resolved.<br>
	 */
	@Test
	public void verifyVariablesWithNerdyStuffLikeRecursion()
	{
		try
		{
			variablesMap.clear();
			variablesMap.put("GNU", "${GNU}'s Not UNIX");

			String one = variablesMap.get("GNU");

			variablesMap.put("whatever", "we just want to force map to resolve variables again...");

			String two = variablesMap.get("GNU");

			assertTrue(one.length() < two.length());
		} catch (Error ouch)
		{
			fail(ouch.getMessage());
		}
	}

	/**
	 * Check no infinite loop on indirect recursion.<br>
	 * Check recursive variable grow each time variables are resolved.<br>
	 */
	@Test
	public void verifyVariablesWithNerdyStuffLikeIndirectRecursion()
	{
		try
		{
			variablesMap.put("a", "${found} I am ${b} ");
			variablesMap.put("b", "what ${a}");

			String one = variablesMap.get("a");

			variablesMap.put("whatever", "we just want to force map to resolve variables again...");

			String two = variablesMap.get("a");

			assertTrue(one.length() < two.length());
		} catch (Error ouch)
		{
			fail(ouch.getMessage());
		}
	}

	/**
	 * Check no infinite loop on indirect recursion.<br>
	 * Check recursive variable doesn't grow each time variables are resolved.<br>
	 */
	@Test
	public void verifyDynamicVariablesRecursion()
	{
		try
		{
			variablesMap.put("divideByZero", "${${divideByZero}}");

			String one = variablesMap.get("divideByZero");

			variablesMap.put("whatever", "we just want to force map to resolve variables again...");

			String two = variablesMap.get("divideByZero");
			// Variable must have the same length here
			assertTrue(one.length() == two.length());
		} catch (Error ouch)
		{
			fail(ouch.getMessage());
		}
	}

	/**
	 * Check we can use the dollar symbol alone in variable
	 */
	@Test
	public void verifyDollarValue()
	{
		assertEquals("$", variablesMap.get("dollarSymbol"));
		assertEquals("$$$ Prey the $ god! $$$", variablesMap.get("dollarGod"));
	}

	/**
	 * Test variables removal.
	 */
	@Test
	public void verifyVariablesRemoval()
	{
		assertEquals("delegated value", variablesMap.get("delegate"));
		variablesMap.remove("real");
		assertEquals("fallback value", variablesMap.get("delegate"));
	}

	/**
	 * Test parser change
	 */
	@Test
	public void verifyParserChange()
	{
		assertEquals("One", variablesMap.get("prop.1"));
		variablesMap.setParser(new DummyParser());
		assertEquals("enO", variablesMap.get("prop.1"));
		variablesMap.setParser(new AntStyleParser());
		assertEquals("One", variablesMap.get("prop.1"));
	}

	/**
	 * Test trimming on variable key
	 */
	@Test
	public void verifyTrimOnKey()
	{
		assertEquals(variablesMap.get("trimKey"), variablesMap.get("notrimKey"));

		variablesMap.put("trimedDefault", "${not.found|default}");
		variablesMap.put("untrimedDefault", "${not.found|    default	  }");
		variablesMap.put("untrimedDynamic", "${${prop.3}}");
		variablesMap.put("trimedDynamic", "${   ${   prop3		}	}");
	}

	/**
	 * Test trimming on variable default value
	 */
	@Test
	public void verifyTrimOnDefault()
	{
		assertNull(variablesMap.get("not.found"));
		assertEquals(variablesMap.get("trimDefault"), variablesMap.get("notrimDefault"));
		variablesMap.put("not.found", "Surprise!");
		assertEquals("Surprise!", variablesMap.get("trimDefault"));
		assertEquals(variablesMap.get("trimDefault"), variablesMap.get("notrimDefault"));
	}

	/**
	 * Test trimming on dynamic variable
	 */
	@Test
	public void verifyTrimOnDynamic()
	{
		assertEquals("yeah", variablesMap.get("trimDynamic"));
		assertEquals(variablesMap.get("trimDynamic"), variablesMap.get("notrimDynamic"));
	}

	/**
	 * Test trimming on recursive variable
	 */
	@Test
	public void verifyTrimOnRecursion()
	{
		try
		{
			variablesMap.put("GNU", "${ GNU   	}'s Not UNIX");

		} catch (Error ouch)
		{
			fail(ouch.getMessage());
		}
	}

	/**
	 * Test syntax check for incorrect variable value
	 */
	@Test
	public void verifySyntaxErrorMissingClosedBracket()
	{
		verifySyntaxError("${foo");
		verifySyntaxError("${");
		verifySyntaxError("${${ds}");
		verifySyntaxError("${fd${fdfd}");
		verifySyntaxError("${fd|${fdfd}");
		verifySyntaxError("dsd ${fd");
	}

	private void verifySyntaxError( String value )
	{
		try
		{
			variablesMap.put("mustFail", value);
			fail(format("Expected an IllegalArgumentException for syntaxically incorrect variable value ''{0}''.", value));
		} catch (IllegalArgumentException expected)
		{
			// ok
		}
	}
}
