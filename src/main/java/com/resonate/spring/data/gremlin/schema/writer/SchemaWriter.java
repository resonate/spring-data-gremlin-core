package com.resonate.spring.data.gremlin.schema.writer;

import com.resonate.spring.data.gremlin.schema.GremlinSchema;
import com.resonate.spring.data.gremlin.tx.GremlinGraphFactory;

/**
 * Interface defining schema writer implementations.
 *
 * @author Gman
 */
public interface SchemaWriter {

    void writeSchema(GremlinGraphFactory dbf, GremlinSchema<?> schema) throws SchemaWriterException;

}
