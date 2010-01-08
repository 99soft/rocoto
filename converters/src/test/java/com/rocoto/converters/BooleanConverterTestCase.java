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

import lombok.Setter;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
public final class BooleanConverterTestCase extends AbstractConverterTestCase {

    @Setter
    @Inject
    @Named("true")
    private boolean expectedTrue;

    @Setter
    @Inject
    @Named("false")
    private Boolean expectedFalse;

    @Setter
    @Inject
    @Named("trues")
    private boolean[] trues;

    @Setter
    @Inject
    @Named("falses")
    private Boolean[] falses;

    @BeforeClass
    public void setUp() {
        this.init(new AbstractModule() {
            @Override
            protected void configure() {
                this.bindConstant()
                    .annotatedWith(Names.named("true"))
                    .to("y");
                this.bindConstant()
                    .annotatedWith(Names.named("false"))
                    .to("0");
                this.bindConstant()
                    .annotatedWith(Names.named("trues"))
                    .to("true, yes, y, on, 1");
                this.bindConstant()
                    .annotatedWith(Names.named("falses"))
                    .to("false, no, n, off, 0");
            }
        });
    }

    @Test
    public void verifySingles() {
        assert true == this.expectedTrue;
        assert Boolean.FALSE.equals(this.expectedFalse);
    }

    @Test
    public void verifyArrays() {
        for (boolean actual : this.trues) {
            assert true == actual;
        }

        for (Boolean actual : this.falses) {
            assert Boolean.FALSE.equals(actual);
        }
    }

}
