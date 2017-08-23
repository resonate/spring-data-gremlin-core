package com.resonate.spring.data.gremlin.schema.property;

import com.resonate.spring.data.gremlin.schema.property.mapper.GremlinLinkPropertyMapper;
import com.tinkerpop.blueprints.Direction;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Gman
 */
public class GremlinLinkProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinLinkProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinLinkPropertyMapper(), CARDINALITY.ONE_TO_ONE);
    }
}
