package com.resonate.spring.data.gremlin.schema.property;

import com.resonate.spring.data.gremlin.schema.property.mapper.GremlinLinkViaPropertyMapper;
import com.tinkerpop.blueprints.Direction;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Gman
 */
public class GremlinLinkViaProperty<C> extends GremlinRelatedProperty<C> {

    public GremlinLinkViaProperty(Class<C> cls, String name, Direction direction) {
        super(cls, name, direction, new GremlinLinkViaPropertyMapper(), CARDINALITY.ONE_TO_ONE);
    }
}
