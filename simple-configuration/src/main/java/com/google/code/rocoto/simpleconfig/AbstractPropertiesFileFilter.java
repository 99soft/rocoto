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
package com.google.code.rocoto.simpleconfig;

import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;

import lombok.Data;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
@Data
public abstract class AbstractPropertiesFileFilter implements FileFilter {

    private static final String DEFAULT_PATH_SEPARATOR = "/";

    private static final String ZERO_OR_MORE_DIR = "**";

    private static final char ZERO_OR_MORE_CHAR = '*';

    private static final char ONE_CHAR = '?';

    private final String propertiesPattern;

    private final String xmlPropertiesPattern;

    /**
     * {@inheritDoc}
     */
    public boolean accept(File pathname) {
        boolean accepted = pathname.isDirectory()
                || this.isXMLProperties(pathname)
                || this.isProperties(pathname);
        System.err.printf("File %s %s accepted\n", pathname, accepted ? "" : "NOT");
        return accepted;
    }

    /**
     * 
     * @param file
     * @return
     */
    protected final boolean isXMLProperties(File file) {
        return match(this.xmlPropertiesPattern, file.getPath(), true);
    }

    /**
     * 
     * @param file
     * @return
     */
    protected final boolean isProperties(File file) {
        return match(this.propertiesPattern, file.getPath(), true);
    }

    /**
     * Check if a file name matches with the Pattern.
     *
     * @param file
     * @param pattern
     * @return true if the supplied path matched, false otherwise.
     */
    private boolean match(String pattern, String path, boolean fullMatch) {
        if (path.startsWith(DEFAULT_PATH_SEPARATOR) != pattern.startsWith(DEFAULT_PATH_SEPARATOR)) {
            return false;
        }

        String[] pattDirs = toStringArray(pattern);
        String[] pathDirs = toStringArray(path);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxStart];
            if (ZERO_OR_MORE_DIR.equals(patDir)) {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxStart])) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            // Path is exhausted, only match if rest of pattern is * or **'s
            if (pattIdxStart > pattIdxEnd) {
                return (pattern.endsWith(DEFAULT_PATH_SEPARATOR) ?
                        path.endsWith(DEFAULT_PATH_SEPARATOR) : !path.endsWith(DEFAULT_PATH_SEPARATOR));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") &&
                    path.endsWith(DEFAULT_PATH_SEPARATOR)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!ZERO_OR_MORE_DIR.equals(pattDirs[i])) {
                    return false;
                }
            }
            return true;
        } else if (pattIdxStart > pattIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else if (!fullMatch && ZERO_OR_MORE_DIR.equals(pattDirs[pattIdxStart])) {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }

        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxEnd];
            if (ZERO_OR_MORE_DIR.equals(patDir)) {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxEnd])) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!ZERO_OR_MORE_DIR.equals(pattDirs[i])) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if (ZERO_OR_MORE_DIR.equals(pattDirs[i])) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop:
                for (int i = 0; i <= strLength - patLength; i++) {
                    for (int j = 0; j < patLength; j++) {
                        String subPat = (String) pattDirs[pattIdxStart + j + 1];
                        String subStr = (String) pathDirs[pathIdxStart + i + j];
                        if (!matchStrings(subPat, subStr)) {
                            continue strLoop;
                        }
                    }
                    foundIdx = pathIdxStart + i;
                    break;
                }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!ZERO_OR_MORE_DIR.equals(pattDirs[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tests whether or not a string matches against a pattern.
     * The pattern may contain two special characters:<br>
     * '*' means zero or more characters<br>
     * '?' means one and only one character
     * 
     * @param pattern pattern to match against.
     *        Must not be <code>null</code>.
     * @param str string which must be matched against the pattern.
     *        Must not be <code>null</code>.
     * @return <code>true</code> if the string matches against the
     *         pattern, or <code>false</code> otherwise.
     */
    private static boolean matchStrings(String pattern, String str) {
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        char ch;

        boolean containsStar = false;
        for (int i = 0; i < patArr.length; i++) {
            if (ZERO_OR_MORE_CHAR == patArr[i]) {
                containsStar = true;
                break;
            }
        }

        if (!containsStar) {
            // No '*'s, so we make a shortcut
            if (patIdxEnd != strIdxEnd) {
                return false; // Pattern and string do not have the same size
            }
            for (int i = 0; i <= patIdxEnd; i++) {
                ch = patArr[i];
                if (ONE_CHAR != ch) {
                    if (ch != strArr[i]) {
                        return false;// Character mismatch
                    }
                }
            }
            return true; // String matches against pattern
        }

        if (patIdxEnd == 0) {
            return true; // Pattern contains only '*', which matches anything
        }

        // Process characters before first star
        while (ZERO_OR_MORE_CHAR != (ch = patArr[patIdxStart]) && strIdxStart <= strIdxEnd) {
            if (ch != '?') {
                if (ch != strArr[strIdxStart]) {
                    return false;// Character mismatch
                }
            }
            patIdxStart++;
            strIdxStart++;
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (ZERO_OR_MORE_CHAR != patArr[i]) {
                    return false;
                }
             }
            return true;
         }

        // Process characters after last star
        while (ZERO_OR_MORE_CHAR != (ch = patArr[patIdxEnd]) && strIdxStart <= strIdxEnd) {
            if (ONE_CHAR != ch) {
                if (ch != strArr[strIdxEnd]) {
                    return false;// Character mismatch
                }
            }
            patIdxEnd--;
            strIdxEnd--;
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (ZERO_OR_MORE_CHAR != patArr[i]) {
                    return false;
                }
            }
            return true;
        }

        // process pattern between stars. padIdxStart and patIdxEnd point
        // always to a '*'.
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
                if (ZERO_OR_MORE_CHAR == patArr[i]) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                // Two stars next to each other, skip the first one.
                patIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - patIdxStart - 1);
            int strLength = (strIdxEnd - strIdxStart + 1);
            int foundIdx = -1;
            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    ch = patArr[patIdxStart + j + 1];
                    if (ONE_CHAR != ch) {
                        if (ch != strArr[strIdxStart + i + j]) {
                            continue strLoop;
                        }
                    }
                }

                foundIdx = strIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }

        // All characters in the string are used. Check if only '*'s are left
        // in the pattern. If so, we succeeded. Otherwise failure.
        for (int i = patIdxStart; i <= patIdxEnd; i++) {
            if (ZERO_OR_MORE_CHAR != patArr[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 
     * @param s
     * @return
     */
    private static String[] toStringArray(String s) {
        StringTokenizer tokenizer = new StringTokenizer(s, DEFAULT_PATH_SEPARATOR);
        String[] split = new String[tokenizer.countTokens()];

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            split[i++] = tokenizer.nextToken().trim();
        }

        return split;
    }

}
