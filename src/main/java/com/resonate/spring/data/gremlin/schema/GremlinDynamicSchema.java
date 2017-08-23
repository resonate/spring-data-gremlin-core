package com.resonate.spring.data.gremlin.schema;

import com.tinkerpop.blueprints.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.resonate.spring.data.gremlin.repository.GremlinGraphAdapter;
import com.resonate.spring.data.gremlin.schema.property.accessor.GremlinFieldPropertyAccessor;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;


/**
 * <p>
 * Defines the schema of a mapped Class. Each GremlinSchema holds the {@code className}, {@code classType},
 * {@code schemaType} (VERTEX, EDGE) and the identifying {@link GremlinFieldPropertyAccessor}.
 * </p>
 * <p>
 * The GremlinSchema contains the high level logic for converting Vertices to mapped classes.
 * </p>
 *
 * @author Gman
 */
public class GremlinDynamicSchema<V> extends GremlinVertexSchema<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinDynamicSchema.class);

    public GremlinDynamicSchema(Class<V> classType) {
        super(classType);
    }

    public void cascadeCopyToGraph(GremlinGraphAdapter graphAdapter, Element element, final Object obj, Map<Object, Element> noCascadingMap) {

        if (noCascadingMap.containsKey(obj)) {
            return;
        }
        noCascadingMap.put(obj, element);

        if (obj instanceof Map) {
            Map<String, Object> map = (Map) obj;
            for (String key : map.keySet()) {
                if (key.equals("_id_")) {
                    continue;
                }
                Object val = map.get(key);
                if (val != null) {
                    element.setProperty(key, val);
                }
            }

            for (String key : element.getPropertyKeys()) {
                if (!map.containsKey(key)) {
                    element.removeProperty(key);
                }
            }

        }
        final Element finalElement = element;

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    setObjectId(obj, finalElement);
                }
            });
        }
    }

    @Override
    public V cascadeLoadFromGraph(GremlinGraphAdapter graphAdapter, Element element, Map<Object, Object> noCascadingMap) {

        Map<String, Object> obj = (Map<String, Object>) super.cascadeLoadFromGraph(graphAdapter, element, noCascadingMap);
        for (String key : element.getPropertyKeys()) {
            obj.put(key, element.getProperty(key));
        }
        return (V) obj;
    }
}
