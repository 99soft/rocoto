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
package com.rocoto.converters;

import java.net.URL;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
public final class ConvertersModuleTestCase {

    private FakeConfig fakeConfig;

    @BeforeClass
    public void setUp() {
        Injector injector = Guice.createInjector(new ConvertersModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        this.bindConstant()
                            .annotatedWith(Names.named("url"))
                            .to("http://code.google.com/");
                    }
                });
        this.fakeConfig = injector.getInstance(FakeConfig.class);
    }

    @AfterClass
    public void tearDown() {
        this.fakeConfig = null;
    }

    @Test
    public void urlConversion() throws Exception {
        assert new URL("http://code.google.com/").equals(this.fakeConfig.getUrl());
    }

}
