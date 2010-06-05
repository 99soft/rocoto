/*
 *    Copyright 2009-2010 The Rocoto Team
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
package com.googlecode.rocoto.simpleconfig;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.googlecode.rocoto.simpleconfig.Formatter;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class FormatterTestCase {

    private final Map<String, String> params = new HashMap<String, String>();

    @BeforeTest
    public void setUp() {
        this.params.put("name", "Michael Schumacher");
        this.params.put("points", "100");
    }

    @Test
    public void verifyFormat() {
        String expected = "The pilot Michael Schumacher has 100 points";
        Formatter formatter = new Formatter("The pilot ${name} has ${points} points");
        System.err.println(formatter);
        String actual = formatter.format(this.params);
        assert expected.equals(actual) : "Expected <"
            + expected
            + "> but found <"
            + actual
            + ">";
    }

}
