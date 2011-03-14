/*
 *    Copyright 2009-2010 The 99 Software Foundation
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
package org.nnsoft.guice.rocoto.converters;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 */
abstract class AbstractTestCase<T> {

    private T convertedField;

    protected void setConvertedField(T convertedField) {
        this.convertedField = convertedField;
    }

    @BeforeClass
    protected final void init() {
        Injector injector = Guice.createInjector(this.getModules());
        injector.injectMembers(this);
    }

    protected abstract Module[] getModules();

    protected final void verifyConversion(T expected) {
        Assert.assertEquals(expected, this.convertedField);
    }

}
