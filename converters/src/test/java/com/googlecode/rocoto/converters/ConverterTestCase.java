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
package com.googlecode.rocoto.converters;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Locale;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.googlecode.rocoto.converters.ConvertersModule;

/**
 * 
 * @author Simone Tripodi
 * @version $Id$
 */
public final class ConverterTestCase {

    @Inject
    @Named("bitset")
    private BitSet bitSet;

    @Inject
    @Named("charset")
    private Charset charset;

    @Inject
    @Named("file")
    private File file;

    @Inject
    @Named("locale")
    private Locale locale;

    @Inject
    @Named("properties")
    private Properties properties;

    @Inject
    @Named("classpathResource")
    private URL classpathResource;

    public void setBitSet(BitSet bitSet) {
        this.bitSet = bitSet;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setClasspathResource(URL classpathResource) {
        this.classpathResource = classpathResource;
    }

    @BeforeClass
    protected final void init() {
        Injector injector = Guice.createInjector(new ConvertersModule(), new AbstractModule() {
            @Override
            protected void configure() {
                this.bindConstant()
                    .annotatedWith(Names.named("bitset"))
                    .to("a, 123, ~");
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
                    .annotatedWith(Names.named("properties"))
                    .to("useUnicode=true\ncharacterEncoding=UTF-8");
                this.bindConstant()
                    .annotatedWith(Names.named("classpathResource"))
                    .to("classpath:///testng.xml");
            }
        });
        injector.injectMembers(this);
    }

    @Test
    public void bitset() {
        BitSet expected = new BitSet();
        expected.set('a');
        expected.set(123);
        expected.set('~');
        Assert.assertEquals(expected, this.bitSet);
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
    public void properties() {
        Properties expected = new Properties();
        expected.setProperty("useUnicode", "true");
        expected.setProperty("characterEncoding", "UTF-8");
        Assert.assertEquals(expected, this.properties);
    }

    @Test
    public void classpathResource() {
        Assert.assertEquals(this.getClass().getResource("/testng.xml"), this.classpathResource);
    }

}
