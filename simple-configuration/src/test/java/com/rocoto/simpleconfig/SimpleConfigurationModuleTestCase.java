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
package com.rocoto.simpleconfig;

import java.io.File;

import org.testng.annotations.Test;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class SimpleConfigurationModuleTestCase {

    private final SimpleConfigurationModule module = new SimpleConfigurationModule();

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void loadNonExistentResource() {
        this.module.addProperties("doesNotExist.properties");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void loadNonExistentXMLResource() {
        this.module.addProperties("doesNotExist.xml");
    }

    @Test
    public void loadFromDir() {
        this.module.addProperties(new File("test-data"));
    }

    @Test
    public void loadFromClasspath() {
        this.module.addProperties("/com/rocoto/simpleconfig/ldap.properties");
    }

}
