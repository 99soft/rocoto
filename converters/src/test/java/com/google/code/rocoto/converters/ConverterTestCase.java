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
package com.google.code.rocoto.converters;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;

import lombok.Setter;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
public final class ConverterTestCase {

    @Setter
    @Inject
    @Named("charset")
    private Charset charset;

    @Setter
    @Inject
    @Named("file")
    private File file;

    @Setter
    @Inject
    @Named("locale")
    private Locale locale;

    @Setter
    @Inject
    @Named("classpathResource")
    private URL classpathResource;

    @BeforeClass
    protected final void init() {
        Injector injector = Guice.createInjector(new ConvertersModule(), new AbstractModule() {
            @Override
            protected void configure() {
                this.bindConstant()
                    .annotatedWith(Names.named("charset"))
                    .to("UTF-8");
                this.bindConstant()
                    .annotatedWith(Names.named("file"))
                    .to("/tmp");
                this.bindConstant()
                    .annotatedWith(Names.named("locale"))
                    .to("en_US");
                this.bindConstant()
                    .annotatedWith(Names.named("classpathResource"))
                    .to("classpath:///testng.xml");
            }
        });
        injector.injectMembers(this);
    }

    @Test
    public void charset() {
        Assert.assertEquals(Charset.forName("UTF-8"), this.charset);
    }

    @Test
    public void file() {
        Assert.assertEquals(new File("/tmp"), this.file);
    }

    @Test
    public void locale() {
        Assert.assertEquals(new Locale("en", "US"), this.locale);
    }

    @Test
    public void classpathResource() {
        Assert.assertEquals(this.getClass().getResource("/testng.xml"), this.classpathResource);
    }

}
