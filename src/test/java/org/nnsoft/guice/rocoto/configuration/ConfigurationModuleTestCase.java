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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @since 6.0
 */
@RunWith( ConfigurationRunner.class )
public final class ConfigurationModuleTestCase
{

	@Inject
	private MyBatisConfiguration myBatisConfiguration;

	@Inject
	private JDBCConfiguration jdbcConfiguration;

	@Inject
	private LdapConfiguration ldapConfiguration;

	@Inject
	private MemcachedConfiguration memcachedConfiguration;

	@Inject
	private ProxyConfiguration proxyConfiguration;

	public void setMyBatisConfiguration( MyBatisConfiguration myBatisConfiguration )
	{
		this.myBatisConfiguration = myBatisConfiguration;
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

	@Test
	public void verifyIBatisConfiguration()
	{
		assertEquals("test", myBatisConfiguration.getEnvironmentId());
		assertTrue(myBatisConfiguration.isLazyLoadingEnabled());
	}

	@Test
	public void verifyJDBCConfiguration()
	{
		assertEquals("com.mysql.jdbc.Driver", jdbcConfiguration.getDriver());
		assertEquals("jdbc:mysql://localhost:3306/rocoto", jdbcConfiguration.getUrl());
		assertEquals("simone", jdbcConfiguration.getUsername());
		assertEquals("rocoto2010", jdbcConfiguration.getPassword());
		assertTrue(jdbcConfiguration.isAutoCommit());
	}

	@Test
	public void verifyLdapConfiguration()
	{
		assertEquals("ldap.${not.found}", ldapConfiguration.getHost());
		assertEquals(389, ldapConfiguration.getPort());
		assertTrue(ldapConfiguration.getBaseDN().indexOf('$') < 0);
		assertEquals("", ldapConfiguration.getUser());
	}

	@Test
	public void verifyMemcachedConfiguration()
	{
		assertEquals("test_", memcachedConfiguration.getKeyPrefix());
		assertTrue(memcachedConfiguration.isCompressionEnabled());
	}

	@Test
	public void verifyProxyConfiguration()
	{
		assertEquals("localhost", proxyConfiguration.getHost());
		assertEquals(8180, proxyConfiguration.getPort());
	}
}
