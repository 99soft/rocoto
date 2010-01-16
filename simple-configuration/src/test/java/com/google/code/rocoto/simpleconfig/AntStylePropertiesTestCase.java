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
package com.google.code.rocoto.simpleconfig;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.code.rocoto.simpleconfig.AntStyleProperties;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class AntStylePropertiesTestCase {

    private final AntStyleProperties properties = new AntStyleProperties();

    @BeforeTest
    public void setUp() {
        this.properties.put("url", "http://${host}/${path}?${params}");
        this.properties.put("host", "${domain}${secondlevel}");
        this.properties.put("domain", "www.javaworld.com");
        this.properties.put("secondlevel", "/javaworld");
        this.properties.put("path", "${first}${second}");
        this.properties.put("first", "jw-03-2002");
        this.properties.put("second", "/jw-0301-dao.html");
        this.properties.put("params", "page=1");
    }

    @Test
    public void verifyProperties() {
        assert "http://www.javaworld.com/javaworld/jw-03-2002/jw-0301-dao.html?page=1".equals(this.properties.get("url"));
    }

}
