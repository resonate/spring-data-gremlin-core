package com.resonate.spring.data.gremlin.support;

import com.resonate.spring.data.gremlin.repository.GremlinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.resonate.spring.data.gremlin.repository.GremlinRepositoryContext;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;

/**
 * Special adapter for Springs {@link org.springframework.beans.factory.FactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 *
 * @param <T> the type of the repository
 * @param <S> the type of the entity
 * @author Gman
 */
public class GremlinRepositoryFactoryBean<T extends GremlinRepository<S>, S> extends TransactionalRepositoryFactoryBeanSupport<T, S, String> {

    /** The orient operations. */
    @Autowired
    private GremlinRepositoryContext context;

    public GremlinRepositoryFactoryBean() {
    }

    /* (non-Javadoc)
         * @see org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport#doCreateRepositoryFactory()
         */
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new GremlinRepositoryFactory(context);
    }
}
