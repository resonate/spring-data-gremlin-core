package com.resonate.spring.data.gremlin.schema.generator;

import com.resonate.spring.data.gremlin.schema.GremlinSchema;

import java.util.Map;
import java.util.Set;

/**
 * An interface defining schema generators.
 *
 * @author Gman
 */
public interface SchemaGenerator {

    <V> GremlinSchema<V> generateSchema(Class<V> clazz) throws SchemaGeneratorException;

    <V> GremlinSchema<V> generateDynamicSchema(String className, Class<? extends Map> mapType);

    void setVertexClasses(Set<Class<?>> entities);

    void setVertexClasses(Class<?>... entites);

    void setEmbeddedClasses(Set<Class<?>> embedded);

    void setEmbeddedClasses(Class<?>... embedded);

    void setEdgeClasses(Set<Class<?>> relationshipClasses);

    void setEdgeClasses(Class<?>... relationshipClasses);
}
