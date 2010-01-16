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

import com.google.inject.Inject;
import com.google.inject.name.Named;

import lombok.Data;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
@Data
public final class LdapConfiguration {

    @Inject
    @Named("ldap.host")
    private String host;

    @Inject
    @Named("ldap.port")
    private int port;

    @Inject
    @Named("ldap.baseDN")
    private String baseDN;

}
