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
package org.nnsoft.guice.rocoto.configuration;

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 */
public final class JDBCConfiguration
{

    @Inject
    @Named( "JDBC.driver" )
    private String driver;

    @Inject
    @Named( "JDBC.url" )
    private String url;

    @Inject
    @Named( "JDBC.username" )
    private String username;

    @Inject
    @Named( "JDBC.password" )
    private String password;

    @Inject
    @Named( "JDBC.autoCommit" )
    private boolean autoCommit;

    public String getDriver()
    {
        return driver;
    }

    public void setDriver( String driver )
    {
        this.driver = driver;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public boolean isAutoCommit()
    {
        return autoCommit;
    }

    public void setAutoCommit( boolean autoCommit )
    {
        this.autoCommit = autoCommit;
    }

}
