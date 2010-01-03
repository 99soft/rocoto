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
package com.rocoto.configuration;

import lombok.Data;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
@Data
public final class MemcachedConfiguration {

    @Inject
    @Named("com.ibaguice.memcached.keyprefix")
    private String keyPrefix;

    @Inject
    @Named("com.ibaguice.memcached.compression")
    private boolean compressionEnabled;

}
