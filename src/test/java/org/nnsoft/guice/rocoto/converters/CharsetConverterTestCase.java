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
package org.nnsoft.guice.rocoto.converters;

import static com.google.inject.name.Names.named;

import java.nio.charset.Charset;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;

/**
 *
 */
public final class CharsetConverterTestCase
    extends AbstractTestCase<Charset>
{

    @Override
    @Inject
    public void setConvertedField( @Named( "charset" ) Charset convertedField )
    {
        super.setConvertedField( convertedField );
    }

    @Override
    protected Module[] getModules()
    {
        return new Module[] { new CharsetConverter(), new AbstractModule()
        {
            protected void configure()
            {
                bindConstant().annotatedWith( named( "charset" ) ).to( "UTF-8" );
            };
        } };
    }

    @Test
    public void charset()
    {
        verifyConversion( Charset.forName( "UTF-8" ) );
    }

}
