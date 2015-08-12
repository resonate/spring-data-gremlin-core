package org.springframework.data.gremlin.schema.property.mapper;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.gremlin.repository.GremlinGraphAdapter;
import org.springframework.data.gremlin.schema.GremlinSchema;
import org.springframework.data.gremlin.schema.property.GremlinRelatedProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A concrete {@link GremlinPropertyMapper} mapping a vertices property to a Collection.
 *
 * @author Gman
 */
public class GremlinCollectionPropertyMapper implements GremlinPropertyMapper<GremlinRelatedProperty, Vertex> {

    @Override
    public void copyToVertex(GremlinRelatedProperty property, GremlinGraphAdapter graphAdapter, Vertex vertex, Object val, Map<Object, Object> cascadingSchemas) {


        // Get the Set of existing linked vertices for this property
        Set<Vertex> existingLinkedVertices = new HashSet<Vertex>();
        Set<Vertex> actualLinkedVertices = new HashSet<Vertex>();
        for (Edge currentEdge : vertex.edges(Direction.IN, property.getName())) {
            existingLinkedVertices.add(edges.next().outVertex())
        }

        // Now go through the collection of linked Objects
        for (Object linkedObj : (Collection) val) {

            // Find the linked vertex mapped to this linked Object
            Vertex linkedVertex = (Vertex) cascadingSchemas.get(linkedObj);
            if (linkedVertex == null) {
                String id = property.getRelatedSchema().getGraphId(linkedObj);
                if (id != null) {
                    linkedVertex = graphAdapter.getVertex(id);
                }
                if (linkedVertex == null) {
                    // No linked vertex yet, create it
                    linkedVertex = graphAdapter.createVertex(property.getRelatedSchema());
                }
            }

            // If this linked Object is new it will not be in the existingLinkedVertices Set
            if (!existingLinkedVertices.contains(linkedVertex)) {
                // New linked Object - add an Edge
                if (property.getDirection() == Direction.OUT) {
                    graphAdapter.addEdge(null, vertex, linkedVertex, property.getName());
                } else {
                    graphAdapter.addEdge(null, linkedVertex, vertex, property.getName());
                }
                // Add to existingLinkedVertices so to not delete it later on when cascading deletes
                existingLinkedVertices.add(linkedVertex);
            }

            // Add the linkedVertex to the actual linked vertices.
            actualLinkedVertices.add(linkedVertex);

            // Updates or saves the linkedObj into the linkedVertex
            property.getRelatedSchema().cascadeCopyToGraph(graphAdapter, linkedVertex, linkedObj, cascadingSchemas);
        }

        // For each disjointed vertex, remove it and the Edge associated with this property
        for (Vertex vertexToDelete : CollectionUtils.disjunction(existingLinkedVertices, actualLinkedVertices)) {
            for (Edge edge : vertexToDelete.getEdges(Direction.OUT, property.getName())) {
                graphAdapter.removeEdge(edge);
            }
            graphAdapter.removeVertex(vertexToDelete);
        }
    }

    @Override
    public <K> Object loadFromVertex(GremlinRelatedProperty property, Vertex vertex, Map<Object, Object> cascadingSchemas) {
        return loadCollection(property.getRelatedSchema(), property, vertex, cascadingSchemas);
    }

    private <V> Set<V> loadCollection(GremlinSchema<V> schema, GremlinRelatedProperty property, Vertex vertex, Map<Object, Object> cascadingSchemas) {
        Set<V> collection = new HashSet<V>();
        for (Edge outEdge : vertex.getEdges(Direction.IN, property.getName())) {
            Vertex inVertex = outEdge.getVertex(Direction.OUT);
            V linkedObject = schema.cascadeLoadFromVertex(inVertex, cascadingSchemas, property.getSchema());
            collection.add(linkedObject);
        }
        return collection;
    }
}