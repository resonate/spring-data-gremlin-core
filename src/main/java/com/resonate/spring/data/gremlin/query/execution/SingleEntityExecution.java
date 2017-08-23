package com.resonate.spring.data.gremlin.query.execution;

import com.resonate.spring.data.gremlin.query.AbstractGremlinQuery;
import com.resonate.spring.data.gremlin.schema.GremlinSchema;
import com.tinkerpop.blueprints.Vertex;
import com.resonate.spring.data.gremlin.repository.GremlinGraphAdapter;
import com.resonate.spring.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Executes the query to return a single entity.
 *
 * @author Gman
 */
@SuppressWarnings("unchecked")
public class SingleEntityExecution extends AbstractGremlinExecution {

    /**
     * Instantiates a new {@link CountExecution}.
     */
    public SingleEntityExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters, GremlinGraphAdapter graphAdapter) {
        super(schemaFactory, parameters, graphAdapter);
    }

    @Override
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {
        Class<?> mappedType = query.getQueryMethod().getReturnedObjectType();

        Iterator<Vertex> result = ((Iterable<Vertex>) query.runQuery(parameters, values)).iterator();
        if (!result.hasNext()) {
            return null;
        }
        Vertex vertex;
        try {
            vertex = result.next();
        } catch (NoSuchElementException e) {
            return null;
        }

        if (vertex == null) {
            return null;
        }
        if (result.hasNext()) {
            throw new IllegalArgumentException("The query resulted in multiple Vertices. Expected only one result for this Execution.");
        }

        if (mappedType.isAssignableFrom(Map.class)) {

            Map<String, Object> map = new HashMap<String, Object>();
            for (String key : vertex.getPropertyKeys()) {
                map.put(key, vertex.getProperty(key));
            }
            return map;
        } else {
            GremlinSchema mapper = schemaFactory.getSchema(mappedType);
            return mapper.loadFromGraph(graphAdapter, vertex);

        }
    }
}
