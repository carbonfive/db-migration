package com.carbonfive.db.hibernate.sessionfactory;


import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Metamodel;
import org.hibernate.*;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.stat.Statistics;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class RoutingSessionFactory implements SessionFactory {
    private Map<Object, SessionFactory> sessionFactories = new HashMap<>();
    private EntityManager entityManager = getSessionFactory().createEntityManager();

    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return getSessionFactory().getFilterDefinition(filterName);
    }

    public Session openSession(Connection connection) {
        return getSessionFactory()
                .withOptions()
                .connection(connection)
                .openSession();
    }

    public Session openSession(Interceptor interceptor) throws HibernateException {
        return getSessionFactory()
                .withOptions()
                .interceptor(interceptor)
                .openSession();
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        return getSessionFactory().withOptions()
                .connection(connection)
                .interceptor(interceptor)
                .openSession();
    }

    public Session openSession() throws HibernateException {
        return getSessionFactory().openSession();
    }

    public Session getCurrentSession() throws HibernateException {
        return getSessionFactory().getCurrentSession();
    }

    public MappingMetamodel getClassMetadata(Class persistentClass) throws HibernateException {
        return entityManager.find(MappingMetamodel.class, persistentClass);
    }

    public MappingMetamodel getClassMetadata(String entityName) throws HibernateException {
        ;
        return entityManager.find(MappingMetamodel.class, new String(entityName));
    }

    public Metamodel getCollectionMetadata(String roleName) throws HibernateException {
        return entityManager.find(Metamodel.class, roleName);
    }

    public Metamodel getAllClassMetadata() throws HibernateException {
        return entityManager.getMetamodel();
    }

    public Metamodel getAllCollectionMetadata() throws HibernateException {
        return entityManager.getMetamodel();
    }

    public Statistics getStatistics() {
        return getSessionFactory().getStatistics();
    }

    public void close() throws HibernateException {
        getSessionFactory().close();
    }

    public boolean isClosed() {
        return getSessionFactory().isClosed();
    }

    public void evict(Class persistentClass) throws HibernateException {
        getSessionFactory().getCache().evict(persistentClass);
    }

    public void evict(Class persistentClass, Serializable id) throws HibernateException {
        getSessionFactory().getCache().evict(persistentClass, id);
    }

    public void evictEntity(String entityName) throws HibernateException {
        getSessionFactory().getCache().evictEntityData(entityName);
    }

    public void evictEntity(String entityName, Serializable id) throws HibernateException {
        getSessionFactory().getCache().evictEntityData(entityName, id);
    }

    public void evictCollection(String roleName) throws HibernateException {
        getSessionFactory().getCache().evictCollectionData(roleName);
    }

    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        getSessionFactory().getCache().evictCollectionData(roleName, id);
    }

    public void evictQueries() throws HibernateException {
        getSessionFactory().getCache().evictQueryRegions();
    }

    public void evictQueries(String cacheRegion) throws HibernateException {
        getSessionFactory().getCache().evictQueryRegion(cacheRegion);
    }

    public StatelessSession openStatelessSession() {
        return getSessionFactory().openStatelessSession();
    }

    public StatelessSession openStatelessSession(Connection connection) {
        return getSessionFactory().openStatelessSession(connection);
    }

    public Set getDefinedFilterNames() {
        return getSessionFactory().getDefinedFilterNames();
    }

    public Reference getReference() throws NamingException {
        return getSessionFactory().getReference();
    }

    public SessionFactory getSessionFactory() {
        SessionFactory sessionFactory = sessionFactories.get(determineCurrentSessionFactoryKey());

        if (sessionFactory == null) {
            throw new HibernateException("Session Factory is null");
        }

        return sessionFactory;
    }

    protected abstract String determineCurrentSessionFactoryKey();

}
