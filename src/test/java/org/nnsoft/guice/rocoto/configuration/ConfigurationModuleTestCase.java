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

import static com.google.inject.Guice.createInjector;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public final class ConfigurationModuleTestCase
{

    @Inject
    private IBatisConfiguration iBatisConfiguration;

    @Inject
    private JDBCConfiguration jdbcConfiguration;

    @Inject
    private LdapConfiguration ldapConfiguration;

    @Inject
    private MemcachedConfiguration memcachedConfiguration;

    @Inject
    private ProxyConfiguration proxyConfiguration;

    public void setiBatisConfiguration( IBatisConfiguration iBatisConfiguration )
    {
        this.iBatisConfiguration = iBatisConfiguration;
    }

    public void setJdbcConfiguration( JDBCConfiguration jdbcConfiguration )
    {
        this.jdbcConfiguration = jdbcConfiguration;
    }

    public void setLdapConfiguration( LdapConfiguration ldapConfiguration )
    {
        this.ldapConfiguration = ldapConfiguration;
    }

    public void setMemcachedConfiguration( MemcachedConfiguration memcachedConfiguration )
    {
        this.memcachedConfiguration = memcachedConfiguration;
    }

    public void setProxyConfiguration( ProxyConfiguration proxyConfiguration )
    {
        this.proxyConfiguration = proxyConfiguration;
    }

    @Before
    public void doInject()
    {
        createInjector( new ConfigurationModule()
        {

            @Override
            protected void bindConfigurations()
            {
                bindEnvironmentVariables();
                bindSystemProperties();

                bindProperties( URI.create( "classpath:/org/nnsoft/guice/rocoto/configuration/ldap.properties" ) );
                bindProperties( "proxy.xml" ).inXMLFormat();

                File parentConf = new File( "src/test/data/org/nnsoft" );
                bindProperties( new File( parentConf, "ibatis.properties" ) );
                bindProperties( new File( parentConf, "guice/jdbc.properties" ) );
                bindProperties( new File( parentConf, "guice/rocoto/configuration/memcached.xml" ) ).inXMLFormat();
            }

        } ).injectMembers( this );
    }

    @Test
    public void verifyIBatisConfiguration()
    {
        assertEquals( "test", iBatisConfiguration.getEnvironmentId() );
        assertTrue( iBatisConfiguration.isLazyLoadingEnabled() );
    }

    @Test
    public void verifyJDBCConfiguration()
    {
        assertEquals( "com.mysql.jdbc.Driver", jdbcConfiguration.getDriver() );
        assertEquals( "jdbc:mysql://localhost:3306/rocoto", jdbcConfiguration.getUrl() );
        assertEquals( "simone", jdbcConfiguration.getUsername() );
        assertEquals( "rocoto2010", jdbcConfiguration.getPassword() );
        assertTrue( jdbcConfiguration.isAutoCommit() );
    }

    @Test
    public void verifyLdapConfiguration()
    {
        assertEquals( "ldap.${not.found}", ldapConfiguration.getHost() );
        assertEquals( 389, ldapConfiguration.getPort() );
        assertTrue( ldapConfiguration.getBaseDN().indexOf( '$' ) < 0 );
        assertEquals( "", ldapConfiguration.getUser() );
    }

    @Test
    public void verifyMemcachedConfiguration()
    {
        assertEquals( "test_", memcachedConfiguration.getKeyPrefix() );
        assertTrue( memcachedConfiguration.isCompressionEnabled() );
    }

    @Test
    public void verifyProxyConfiguration()
    {
        assertEquals( "localhost", proxyConfiguration.getHost() );
        assertEquals( 8180, proxyConfiguration.getPort() );
    }

}
