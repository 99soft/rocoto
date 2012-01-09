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

import static com.google.inject.name.Names.named;
import static com.google.inject.spi.Elements.getElements;
import static com.google.inject.util.Modules.override;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map.Entry;

import org.nnsoft.guice.rocoto.configuration.variables.VariablesMap;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;

/**
 * @since 6.0
 */
public final class Rocoto
    extends AbstractModule
{

    public static Module expandVariables( Module...baseModules )
    {
        return expandVariables( asList( baseModules ) );
    }

    public static Module expandVariables( Iterable<? extends Module> baseModules )
    {
        return override( baseModules ).with( new Rocoto( getElements( baseModules ) ) );
    }

    private final TypeLiteral<String> stringLiteral = new TypeLiteral<String>(){};

    private final List<Element> elements;

    /**
     * Do nothing, this class cannot be instantiated
     */
    private Rocoto( List<Element> elements )
    {
        this.elements = elements;
    }

    @Override
    protected void configure()
    {
        final VariablesMap variablesMap = new VariablesMap();

        for ( final Element element : elements )
        {
            element.acceptVisitor( new DefaultElementVisitor<Void>()
            {

                @Override
                public <T> Void visit( Binding<T> binding )
                {
                    Key<?> bindingKey = binding.getKey();

                    if ( stringLiteral.equals( bindingKey.getTypeLiteral() )
                            && bindingKey.getAnnotation() != null
                            && ( Named.class.isAssignableFrom( bindingKey.getAnnotationType() )
                                            || javax.inject.Named.class.isAssignableFrom( bindingKey.getAnnotationType() ) ) )
                    {
                        String propertyKey;

                        if ( Named.class.isAssignableFrom( bindingKey.getAnnotationType() ) )
                        {
                            propertyKey = ( (Named) bindingKey.getAnnotation() ).value();
                        }
                        else
                        {
                            propertyKey = ( (javax.inject.Named) bindingKey.getAnnotation() ).value();
                        }

                        String propertyValue = (String) binding.getProvider().get();

                        variablesMap.put( propertyKey, propertyValue );
                    }

                    return super.visit( binding );
                }

            });
        }

        for ( Entry<String, String> variable : variablesMap.entrySet() )
        {
            bindConstant().annotatedWith( named( variable.getKey() ) ).to( variable.getValue() );
        }
    }

}
