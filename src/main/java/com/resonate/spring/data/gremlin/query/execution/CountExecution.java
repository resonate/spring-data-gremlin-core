package com.resonate.spring.data.gremlin.query.execution;

import com.resonate.spring.data.gremlin.query.AbstractGremlinQuery;
import com.tinkerpop.blueprints.Vertex;
import com.resonate.spring.data.gremlin.repository.GremlinGraphAdapter;
import com.resonate.spring.data.gremlin.schema.GremlinSchemaFactory;
import org.springframework.data.repository.query.DefaultParameters;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Executes the query to return the sum of entities.
 *
 * @author Gman
 */
@SuppressWarnings("unchecked")
public class CountExecution extends AbstractGremlinExecution {

    /**
     * Instantiates a new {@link CountExecution}.
     */
    public CountExecution(GremlinSchemaFactory schemaFactory, DefaultParameters parameters, GremlinGraphAdapter graphAdapter) {
        super(schemaFactory, parameters, graphAdapter);
    }

    /* (non-Javadoc)
         * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
         */
    @Override
    protected Object doExecute(AbstractGremlinQuery query, Object[] values) {
        Iterator<Vertex> result = ((Iterable<Vertex>) query.runQuery(parameters, values, true)).iterator();
        long counter = 0L;

        try {
            while (true) {
                result.next();
                ++counter;
            }
        } catch (NoSuchElementException var4) {
            return counter;
        }
    }
}
