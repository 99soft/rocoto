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
package com.googlecode.rocoto.system;

import java.lang.annotation.Annotation;

/**
 * Abstract representation of an init-param annotation.
 *
 * @author Simone Tripodi (simone.tripodi)
 * @version $Id$
 */
abstract class AbstractInitParamImpl implements Annotation {

    /**
     * The <code>value</code> String constant.
     */
    private static final String VALUE = "value";

    /**
     * This init-param name.
     */
    private final String name;

    /**
     * This init-param type.
     */
    private final Class<? extends Annotation> annotationType;

    /**
     * Instantiate a new init-param representation, sub-classes has to specify
     * the annotation type.
     *
     * @param name the init-param name.
     * @param annotationType the init-param type.
     */
    @SuppressWarnings("unchecked")
    public AbstractInitParamImpl(final String name) {
        this.name = name;
        this.annotationType = (Class<? extends Annotation>) this.getClass().getInterfaces()[0];
    }

    /**
     * Return this init-param name.
     *
     * @return this init-param name.
     */
    public final String value() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    public final int hashCode() {
        return (127 * VALUE.hashCode() ^ this.name.hashCode());
    }

    /**
     * {@inheritDoc}
     */
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!this.annotationType.isInstance(obj)) {
            return false;
        }

        AbstractInitParamImpl other = (AbstractInitParamImpl) obj;
        return this.name.equals(other.value());
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return "@"
            + this.annotationType.getName()
            + "(value="
            + this.name
            + ")";
    }

    /**
     * {@inheritDoc}
     */
    public final Class<? extends Annotation> annotationType() {
        return this.annotationType;
    }

}
