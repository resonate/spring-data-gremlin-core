package org.springframework.data.gremlin.schema.property.mapper;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinAdjacentProperty;
import org.springframework.data.gremlin.schema.property.GremlinLinkProperty;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;
import org.springframework.util.Assert;

import java.util.*;

/**
 * A {@link GremlinPropertyMapper} for mapping {@link GremlinLinkProperty}s. There are 2 configurable properties for this property mapper:
 * <ul>
 * <li>boolean linkViaEdge - set to true if this link maps a vertex to an edge. If false, a vertex to vertex is assumed.</li>
 * <li>{@link Direction} direction - The direction of the link associated with this property mapper</li>
 * </ul>
 *
 * @author Gman
 */
public class GremlinCollectionViaPropertyMapper extends GremlinLinkPropertyMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinCollectionViaPropertyMapper.class);

    @Override
    public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Map<Object, Object> cascadingSchemas) {
        Assert.notNull(vertex);

        // Get the Set of existing linked vertices for this property
        Set<Edge> existingLinkedEdges = new HashSet<>();
        Set<Edge> actualLinkedEdges = new HashSet<>();
        for (Edge currentEdge : vertex.getEdges(property.getDirection(), property.getRelatedSchema().getClassName())) {
            existingLinkedEdges.add(currentEdge);
        }

        GremlinAdjacentProperty adjacentProperty = property.getAdjacentProperty();

        // Check we found the adjacent property
        if (adjacentProperty != null) {

            // Now go through the collection of linked Objects
            for (Object linkedObj : (Collection) val) {

                Object adjacentObj = adjacentProperty.getAccessor().get(linkedObj);
                if (adjacentObj != null) {
                    Vertex adjacentVertex = graphAdapter.findOrCreateVertex(adjacentProperty.getRelatedSchema().getObjectId(adjacentObj), adjacentProperty.getRelatedSchema().getClassName());

                    // If we have the adjacent vertex then we can continue
                    if (adjacentVertex != null) {


                        Edge linkedEdge = null;

                        // get the current edge for this property
                        Iterator<Edge> edges = vertex.getEdges(property.getDirection(), property.getRelatedSchema().getClassName()).iterator();
                        while (edges.hasNext()) {
                            Edge edge = edges.next();
                            if (edge.getVertex(property.getDirection().opposite()).equals(adjacentVertex)) {
                                existingLinkedEdges.add(edge);
                                linkedEdge = edge;
                                break;
                            }
                        }

                        if (linkedEdge == null) {
                            if (property.getDirection() == Direction.OUT) {
                                linkedEdge = graphAdapter.addEdge(null, vertex, adjacentVertex, property.getRelatedSchema().getClassName());
                            } else {
                                linkedEdge = graphAdapter.addEdge(null, adjacentVertex, vertex, property.getRelatedSchema().getClassName());
                            }
                            property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedEdge, linkedObj, cascadingSchemas);
                        }

                        existingLinkedEdges.add(linkedEdge);
                        actualLinkedEdges.add(linkedEdge);


                        if(Boolean.getBoolean(CASCADE_ALL_KEY) || property.getDirection() == Direction.OUT) {
                            LOGGER.debug("Cascading copy of " + property.getRelatedSchema().getClassName());
                            // Updates or saves the val into the linkedVertex
                            adjacentProperty.getRelatedSchema().cascadeCopyToGraph(graphAdapter, adjacentVertex, adjacentObj, cascadingSchemas);
                        }
                    }
                }
            }
        }

        // For each disjointed edge, remove it
        for (Edge vertexToDelete : CollectionUtils.disjunction(existingLinkedEdges, actualLinkedEdges)) {
            LOGGER.debug("Removing " + vertexToDelete + ".");
            vertexToDelete.remove();
        }

    }


    @Override
    public <K> Object loadFromVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Map<Object, Object> cascadingSchemas) {
        return loadCollection(property.getRelatedSchema(), property, graphAdapter, vertex, cascadingSchemas);
    }

    private <V> Set<V> loadCollection(GremlinSchema<V> schema, GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Map<Object, Object> cascadingSchemas) {
        Set<V> collection = new HashSet<V>();
        for (Edge linkedEdge : vertex.getEdges(property.getDirection(), property.getRelatedSchema().getClassName())) {
            graphAdapter.refresh(linkedEdge);
            V linkedObject = schema.cascadeLoadFromGraph(graphAdapter, linkedEdge, cascadingSchemas);
            collection.add(linkedObject);
        }
        return collection;
    }
}
