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
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
final class PropertiesFileFilter implements FileFilter {

    private static final Pattern PROPERTIES_PATTERN = Pattern.compile(".*\\.properties", Pattern.CASE_INSENSITIVE);

    private static final Pattern XML_PROPERTIES_PATTERN = Pattern.compile(".*\\.xml", Pattern.CASE_INSENSITIVE);

    /**
     * {@inheritDoc}
     */
    public boolean accept(File pathname) {
        return pathname.isDirectory()
                || matches(pathname, PROPERTIES_PATTERN)
                || matches(pathname, XML_PROPERTIES_PATTERN);
    }

    /**
     * Check if a file name matches with the Pattern.
     *
     * @param pathname
     * @param pattern
     * @return
     */
    private static boolean matches(File pathname, Pattern pattern) {
        return pattern.matcher(pathname.getName()).matches();
    }

}
