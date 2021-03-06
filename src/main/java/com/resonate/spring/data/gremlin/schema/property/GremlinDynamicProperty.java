package com.resonate.spring.data.gremlin.schema.property;

import com.resonate.spring.data.gremlin.schema.property.mapper.GremlinLinkPropertyMapper;
import com.tinkerpop.blueprints.Direction;

import java.util.Map;

/**
 * A {@link GremlinRelatedProperty} accessor for linked properties (one-to-one relationships).
 *
 * @author Gman
 */
public class GremlinDynamicProperty<C extends Map> extends GremlinRelatedProperty<C> {

    private String relatedClassName;

    public GremlinDynamicProperty(Class<C> cls, String name, String relatedClassName, Direction direction) {
        super(cls, name, direction, new GremlinLinkPropertyMapper(), CARDINALITY.ONE_TO_ONE);
        this.relatedClassName = relatedClassName;
    }

    public String getRelatedClassName() {
        return relatedClassName;
    }
}
