package org.nnsoft.guice.rocoto.mixed;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.name.Names.bindProperties;
import static org.junit.Assert.assertTrue;
import static org.nnsoft.guice.rocoto.configuration.Rocoto.expandVariables;

import java.io.File;
import java.util.Properties;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.nnsoft.guice.rocoto.converters.FileConverter;

import com.google.inject.AbstractModule;
import com.google.inject.name.Named;

public final class ConfigurationConverterTestCase
{

    @Inject
    @Named( "test.suites" )
    private File testSuites;

    @Before
    public void setUp()
    {
        createInjector( expandVariables( new AbstractModule()
        {

            @Override
            protected void configure()
            {
                Properties properties = new Properties();
                properties.put( "test.suites", "${user.dir}/src/test/resources/testng.xml" );

                bindProperties( binder(), properties );
            }

        }, new ConfigurationModule()
        {

            @Override
            protected void bindConfigurations()
            {
                bindSystemProperties();
            }

        } ), new FileConverter() ).injectMembers( this );
    }

    public void setTestSuites( File testSuites )
    {
        this.testSuites = testSuites;
    }

    @Test
    public void verifyFileExists()
    {
        assertTrue( testSuites.exists() );
    }

}
