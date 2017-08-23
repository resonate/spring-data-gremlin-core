package com.resonate.spring.data.gremlin.repository;

import com.resonate.spring.data.gremlin.schema.GremlinEdgeSchema;
import com.resonate.spring.data.gremlin.schema.GremlinSchema;
import com.resonate.spring.data.gremlin.schema.property.GremlinAdjacentProperty;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.resonate.spring.data.gremlin.tx.GremlinGraphFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of the {@link org.springframework.data.repository.PagingAndSortingRepository} interface for Gremlin.
 *
 * @param <T> the type of the entity to handle
 * @author Gman
 */
@Repository
@Transactional(readOnly = true)
public class SimpleGremlinRepository<T> implements GremlinRepository<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleGremlinRepository.class);

    protected GremlinGraphFactory dbf;

    protected GremlinSchema<T> schema;

    protected GremlinGraphAdapter graphAdapter;

    public SimpleGremlinRepository(GremlinGraphFactory dbf, GremlinGraphAdapter graphAdapter, GremlinSchema<T> schema) {
        this.dbf = dbf;
        this.graphAdapter = graphAdapter;
        this.schema = schema;
    }

    //    @Transactional(readOnly = false)
    //    public Element create(Graph graph, final T object) {
    //        final Element element = graphAdapter.createVertex(graph, schema.getClassName());
    //        schema.copyToGraph(graphAdapter, element, object);
    //        if (TransactionSynchronizationManager.isSynchronizationActive()) {
    //            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
    //                @Override
    //                public void afterCommit() {
    //                    schema.setObjectId(object, element);
    //                }
    //            });
    //        }
    //        return element;
    //    }

    @Transactional(readOnly = false)
    private Element create(Graph graph, final T object, Object... noCascade) {
        Element element;
        if (schema.isVertexSchema()) {
            element = graphAdapter.createVertex(graph, schema.getClassName());
            schema.copyToGraph(graphAdapter, element, object, noCascade);
        } else if (schema.isEdgeSchema()) {
            GremlinEdgeSchema edgeSchema = (GremlinEdgeSchema) schema;
            GremlinAdjacentProperty adjacentOutProperty = edgeSchema.getOutProperty();

            Vertex outVertex = null;
            Vertex inVertex = null;

            Object outObject = adjacentOutProperty.getAccessor().get(object);
            if (outObject != null) {
                String outId = adjacentOutProperty.getRelatedSchema().getObjectId(outObject);
                outVertex = graphAdapter.findOrCreateVertex(outId, adjacentOutProperty.getRelatedSchema().getClassName());
            }

            GremlinAdjacentProperty adjacentInProperty = edgeSchema.getInProperty();
            Object inObject = adjacentInProperty.getAccessor().get(object);
            if (inObject != null) {
                String inId = adjacentInProperty.getRelatedSchema().getObjectId(inObject);
                inVertex = graphAdapter.findOrCreateVertex(inId, adjacentInProperty.getRelatedSchema().getClassName());
            }

            element = graphAdapter.addEdge(null, outVertex, inVertex, schema.getClassName());

            schema.copyToGraph(graphAdapter, element, object, noCascade);
        } else {
            throw new IllegalStateException("Schema is neither EDGE nor VERTEX!");
        }
        return element;
    }

    @Transactional(readOnly = false)
    public T save(Graph graph, T object, Object... noCascade) {

        String id = schema.getObjectId(object);
        if (StringUtils.isEmpty(id)) {
            create(graph, object);
        } else {
            Element element;
            if (schema.isVertexSchema()) {
                element = graph.getVertex(schema.decodeId(id));
            } else if (schema.isEdgeSchema()) {
                element = graph.getEdge(schema.decodeId(id));
            } else {
                throw new IllegalStateException("Schema is neither EDGE nor VERTEX!");
            }
            if (element == null) {
                throw new IllegalStateException(String.format("Could not save %s with id %s, as it does not exist.", object, id));
            }
            schema.copyToGraph(graphAdapter, element, object, noCascade);
        }
        return object;
    }

    @Override
    public <S extends T> S save(S entity) {
        return save(entity, new Object[0]);
    }

    @Transactional(readOnly = false)
    @Override
    public <S extends T> S save(final S s, final Object... noCascade) {

        Graph graph = dbf.graph();

        String id = schema.getObjectId(s);
        if (graphAdapter.isValidId(id)) {
            save(graph, s, noCascade);
        } else {
            create(graph, s, noCascade);

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        if (!graphAdapter.isValidId(schema.getObjectId(s))) {
//                            throw dbf.getForceRetryException();
                        }
                    }
                });
            }
        }
        return s;

    }

    @Transactional(readOnly = false)
    @Override
    public <S extends T> Iterable<S> save(Iterable<S> iterable) {
        for (S s : iterable) {
            save(s);
        }
        return iterable;
    }

    @Override
    public T findOne(String id) {
        T object = null;
        Element element;
        if (schema.isVertexSchema()) {
            element = graphAdapter.findVertexById(id);
        } else if (schema.isEdgeSchema()) {
            element = graphAdapter.findEdgeById(id);
        } else {
            throw new IllegalStateException("Schema is neither VERTEX nor EDGE!");
        }

        if (element != null) {
            object = schema.loadFromGraph(graphAdapter, element);
        }

        return object;
    }

    @Override
    public boolean exists(String id) {
        return count() == 1;
    }

    @Override
    public Iterable<T> findAll() {
        throw new NotImplementedException("Finding all vertices in Graph databases does not really make sense. So, it hasn't been implemented.");
    }

    @Override
    public Iterable<T> findAll(Iterable<String> iterable) {
        Set<T> objects = new HashSet<T>();
        for (String id : iterable) {
            objects.add(findOne(id));
        }
        return objects;
    }

    @Override
    public long count() {
        throw new NotImplementedException("Counting all vertices in Gremlin has not been implemented.");
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(String id) {
        if (schema.isVertexSchema()) {
            Vertex v = graphAdapter.findVertexById(id);
            dbf.graph().removeVertex(v);
        } else if (schema.isEdgeSchema()) {
            Edge v = graphAdapter.findEdgeById(id);
            dbf.graph().removeEdge(v);
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(T t) {
        delete(schema.getObjectId(t));
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Iterable<? extends T> iterable) {
        for (T t : iterable) {
            delete(t);
        }
    }

    @Override
    public void deleteAll() {
        throw new NotImplementedException("Deleting all vertices in Gremlin has not been implemented.");
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        throw new NotImplementedException("Sorting all vertices in Gremlin has not been implemented.");
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        throw new NotImplementedException("Deleting all vertices in Gremlin has not been implemented.");
    }


}
