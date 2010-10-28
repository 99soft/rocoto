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
package com.googlecode.rocoto.cli;

/**
 * 
 *
 * @author Simone Tripodi
 * @since 4.0
 * @version $Id$
 */
public final class Option {

    /**
     * constant that specifies the number of argument values has not been specified
     */
    public static final int UNINITIALIZED = -1;

    /**
     * constant that specifies the number of argument values is infinite
     */
    public static final int UNLIMITED_VALUES = -2;

    /**
     * the name of the option
     */
    private final String opt;

    /**
     * the long representation of the option
     */
    private final String longOpt;

    /**
     * the name of the argument for this option
     */
    private String argName;

    /**
     * description of the option
     */
    private final String description;

    /**
     * specifies whether this option is required to be present
     */
    private boolean required;

    /**
     * specifies whether the argument value of this Option is optional
     */
    private boolean optionalArg;

    /**
     * the number of argument values this option can have
     */
    private int numberOfArgs = UNINITIALIZED;

    public Option(String opt, String description) {
        this(opt, null, false, description);
    }

    public Option(String opt, boolean hasArg, String description) {
        this(opt, null, hasArg, description);
    }

    public Option(String opt, String longOpt, boolean hasArg, String description) {
        this.opt = opt;
        this.longOpt = longOpt;
        if (hasArg) {
            this.numberOfArgs = 1;
        }
        this.description = description;
    }

}
