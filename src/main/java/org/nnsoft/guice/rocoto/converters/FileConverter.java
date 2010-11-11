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
package org.nnsoft.guice.rocoto.converters;

import java.io.File;

import com.google.inject.TypeLiteral;

/**
 * Converter implementation for {@code java.io.File}.
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class FileConverter extends AbstractConverter<File> {

    /**
     * {@inheritDoc}
     */
    public Object convert(String value, TypeLiteral<?> toType) {
        return new File(value);
    }

}
