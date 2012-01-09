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
import static org.nnsoft.guice.rocoto.configuration.Rocoto.expandVariables;

import java.io.File;
import java.net.URI;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Injector;

/**
 * @since 6.0
 */
public final class ConfigurationRunner
    extends BlockJUnit4ClassRunner
{

    private final Injector injector;

    public ConfigurationRunner( Class<?> klass )
        throws InitializationError
    {
        super( klass );

        injector = createInjector( expandVariables( new ConfigurationModule()
        {

            @Override
            protected void bindConfigurations()
            {
                bindEnvironmentVariables();
                bindSystemProperties();

                bindProperty( "test.suites" ).toValue( "${user.dir}/src/test/resources/testng.xml" );

                bindProperties( URI.create( "classpath:/org/nnsoft/guice/rocoto/configuration/ldap.properties" ) );
                bindProperties( "proxy.xml" ).inXMLFormat();

                File parentConf = new File( "src/test/data/org/nnsoft" );
                bindProperties( new File( parentConf, "ibatis.properties" ) );
                bindProperties( new File( parentConf, "guice/jdbc.properties" ) );
                bindProperties( new File( parentConf, "guice/rocoto/configuration/memcached.xml" ) ).inXMLFormat();
            }

        } ) );
    }

    @Override
    protected Object createTest()
        throws Exception
    {
        return injector.getInstance( getTestClass().getJavaClass() );
    }

}
