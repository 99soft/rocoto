package org.nnsoft.guice.rocoto.configuration.readers;

import static org.nnsoft.guice.rocoto.configuration.readers.PropertiesIterator.newPropertiesIterator;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public final class MapReader extends AbstractConfigurationReader {

    /**
     * The properties have to be read.
     */
    private final Map<String, String> properties;

    /**
     * Creates a new properties reader adapter.
     *
     * @param properties the properties have to be read.
     */
    public MapReader(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Entry<String, String>> readConfiguration() throws Exception {
        return newPropertiesIterator(getPrefix(), properties);
    }

}
