package com.resonate.spring.data.gremlin.support;

import com.resonate.spring.data.gremlin.schema.property.accessor.GremlinIdPropertyAccessor;
import org.springframework.data.repository.core.support.AbstractEntityInformation;

/**
 * An {@link AbstractEntityInformation} for Gremlin.
 *
 * @param <T> The class type of the entity
 * @author Gman
 */
public class GremlinMetamodelEntityInformation<T> extends AbstractEntityInformation<T, String> {

    private GremlinIdPropertyAccessor idAccessor;

    public GremlinMetamodelEntityInformation(Class<T> domainClass, GremlinIdPropertyAccessor idAccessor) {
        super(domainClass);
        this.idAccessor = idAccessor;
    }

    public String getId(T entity) {
        return idAccessor.get(entity);
    }

    public Class<String> getIdType() {
        return String.class;
    }
}
