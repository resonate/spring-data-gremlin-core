package com.resonate.spring.data.gremlin.schema.property.mapper;

import com.resonate.spring.data.gremlin.schema.property.GremlinAdjacentProperty;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.resonate.spring.data.gremlin.repository.GremlinGraphAdapter;

import java.util.Map;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinAdjacentProperty}s.
 *
 * @author Gman
 */
public class GremlinAdjacentPropertyMapper implements GremlinPropertyMapper<GremlinAdjacentProperty, Edge> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinAdjacentPropertyMapper.class);
    @Override
    public void copyToVertex(GremlinAdjacentProperty property, GremlinGraphAdapter graphAdapter, Edge edge, Object val, Map<Object, Object> cascadingSchemas) {

        Vertex linkedVertex = edge.getVertex(property.getDirection());

        if (linkedVertex == null) {
            linkedVertex = (Vertex) cascadingSchemas.get(val);
        }

        if (linkedVertex != null && (Boolean.getBoolean(CASCADE_ALL_KEY) || property.getDirection() == Direction.OUT)) {
            LOGGER.debug("Cascading copy of " + property.getRelatedSchema().getClassName());
            //             Updates or saves the val into the linkedVertex
            property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, val, cascadingSchemas);
        }

    }

    @Override
    public <K> Object loadFromVertex(GremlinAdjacentProperty property, GremlinGraphAdapter graphAdapter, Edge edge, Map<Object, Object> cascadingSchemas) {
        Object val = null;
        Vertex linkedVertex = edge.getVertex(property.getDirection());
        if (linkedVertex != null) {
            graphAdapter.refresh(linkedVertex);
            val = property.getRelatedSchema().cascadeLoadFromGraph(graphAdapter, linkedVertex, cascadingSchemas);
        }
        return val;
    }
}
