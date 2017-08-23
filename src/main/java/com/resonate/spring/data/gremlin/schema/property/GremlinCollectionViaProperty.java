package com.resonate.spring.data.gremlin.schema.property;

import com.resonate.spring.data.gremlin.schema.property.mapper.GremlinCollectionViaPropertyMapper;
import com.tinkerpop.blueprints.Direction;

/**
 * A concrete {@link GremlinRelatedProperty} for a Collection via a relational entity
 *
 * @author Gman
 */
public class GremlinCollectionViaProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinCollectionViaProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinCollectionViaPropertyMapper(), CARDINALITY.ONE_TO_MANY);
    }
}
