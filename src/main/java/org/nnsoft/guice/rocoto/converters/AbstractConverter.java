/*
 *    Copyright 2009-2011 The 99 Software Foundation
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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;

/**
 * A special Google Guice converter that auto binds itself to the converted type.
 *
 * @param <T> the type managed by this converter.
 */
public abstract class AbstractConverter<T>
    extends TypeLiteral<T>
    implements Module, TypeConverter
{

    /**
     * {@inheritDoc}
     */
    public final void configure( Binder binder )
    {
        binder.convertToTypes( Matchers.only( TypeLiteral.get( this.getRawType() ) ), this );
    }

}
