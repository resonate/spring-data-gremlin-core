package com.resonate.spring.data.gremlin.schema.property.accessor;

import com.resonate.spring.data.gremlin.schema.property.GremlinProperty;

/**
 * Interface defining an accessor of a {@link GremlinProperty}
 *
 * @param <V> The result value type of the accessor
 * @author Gman
 */
public interface GremlinPropertyAccessor<V> {
    V get(Object object);

    void set(Object object, V val);
}
