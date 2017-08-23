package com.resonate.spring.data.gremlin.schema.property;

import com.resonate.spring.data.gremlin.schema.property.mapper.GremlinCollectionPropertyMapper;
import com.tinkerpop.blueprints.Direction;

/**
 * A concrete {@link GremlinRelatedProperty} for a Collection
 *
 * @author Gman
 */
public class GremlinCollectionProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinCollectionProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinCollectionPropertyMapper(), CARDINALITY.ONE_TO_MANY);
    }
}
