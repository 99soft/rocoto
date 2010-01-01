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

import lombok.Setter;

import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * 
 *
 * @author Simone Tripodi
 * @version $Id$
 */
public final class SimpleConfigurationModuleTestCase {

    private final SimpleConfigurationModule module = new SimpleConfigurationModule();

    @Inject
    @Setter
    private IBatisConfiguration iBatisConfiguration;

    @Inject
    @Setter
    private JDBCConfiguration jdbcConfiguration;

    @Inject
    @Setter
    private LdapConfiguration ldapConfiguration;

    @Inject
    @Setter
    private MemcachedConfiguration memcachedConfiguration;

    @Inject
    @Setter
    private ProxyConfiguration proxyConfiguration;

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void loadNonExistentResource() {
        this.module.addProperties("doesNotExist.properties");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void loadNonExistentXMLResource() {
        this.module.addProperties("doesNotExist.xml");
    }

    @Test
    public void loadFromClasspath() {
        this.module.addProperties("/com/rocoto/simpleconfig/ldap.properties");
    }

    @Test
    public void loadFromRootClasspath() {
        this.module.addXMLProperties("proxy.xml");
    }

    @Test
    public void loadFromDirUsingDefaulTraversal() {
        this.module.addProperties(new File("test-data"));
    }

    @Test(dependsOnMethods = {
            "loadNonExistentResource",
            "loadNonExistentXMLResource",
            "loadFromClasspath",
            "loadFromRootClasspath",
            "loadFromDirUsingDefaulTraversal"
    })
    public void doInject() {
        Injector injector = Guice.createInjector(this.module);
        injector.injectMembers(this);
    }

    @Test(dependsOnMethods = "doInject")
    public void verifyIBatisConfiguration() {
        assert this.iBatisConfiguration.getEnvironmentId() != null;
        assert this.iBatisConfiguration.isLazyLoadingEnabled();
    }

    @Test(dependsOnMethods = "doInject")
    public void verifyJDBCConfiguration() {
        assert this.jdbcConfiguration.getDriver() != null;
        assert this.jdbcConfiguration.getUrl() != null;
        assert this.jdbcConfiguration.getUsername() != null;
        assert this.jdbcConfiguration.getPassword() != null;
        assert this.jdbcConfiguration.isAutoCommit();
    }

    @Test(dependsOnMethods = "doInject")
    public void verifyLdapConfiguration() {
        assert this.ldapConfiguration.getHost() != null;
        assert this.ldapConfiguration.getPort() > 0;
        assert this.ldapConfiguration.getBaseDN() != null;
    }

    @Test(dependsOnMethods = "doInject")
    public void verifyMemcachedConfiguration() {
        assert this.memcachedConfiguration.getKeyPrefix() != null;
        assert this.memcachedConfiguration.isCompressionEnabled();
    }

    @Test(dependsOnMethods = "doInject")
    public void verifyProxyConfiguration() {
        assert this.proxyConfiguration.getHost() != null;
        assert this.proxyConfiguration.getPort() > 0;
    }

}
