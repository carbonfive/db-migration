package com.carbonfive.db.hibernate.sessionfactory;

import org.hibernate.*;
import org.hibernate.classic.Session;
import org.hibernate.engine.*;
import org.hibernate.metadata.*;
import org.hibernate.stat.*;

import javax.naming.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public abstract class RoutingSessionFactory implements SessionFactory
{
    //private boolean createNewSessionFactories = true;
    private Map<Object, SessionFactory> sessionFactories = new HashMap<Object, SessionFactory>();
    //private DataSourceFactory dataSourceFactory;

    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException
    {
        return getSessionFactory().getFilterDefinition(filterName);
    }

    public Session openSession(Connection connection)
    {
        return getSessionFactory().openSession(connection);
    }

    public Session openSession(Interceptor interceptor) throws HibernateException
    {
        return getSessionFactory().openSession(interceptor);
    }

    public Session openSession(Connection connection, Interceptor interceptor)
    {
        return getSessionFactory().openSession(connection, interceptor);
    }

    public Session openSession() throws HibernateException
    {
        return getSessionFactory().openSession();
    }

    public Session getCurrentSession() throws HibernateException
    {
        return getSessionFactory().getCurrentSession();
    }

    public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException
    {
        return getSessionFactory().getClassMetadata(persistentClass);
    }

    public ClassMetadata getClassMetadata(String entityName) throws HibernateException
    {
        return getSessionFactory().getClassMetadata(entityName);
    }

    public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException
    {
        return getSessionFactory().getCollectionMetadata(roleName);
    }

    public Map getAllClassMetadata() throws HibernateException
    {
        return getSessionFactory().getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() throws HibernateException
    {
        return getSessionFactory().getAllCollectionMetadata();
    }

    public Statistics getStatistics()
    {
        return getSessionFactory().getStatistics();
    }

    public void close() throws HibernateException
    {
        getSessionFactory().close();
    }

    public boolean isClosed()
    {
        return getSessionFactory().isClosed();
    }

    public void evict(Class persistentClass) throws HibernateException
    {
        getSessionFactory().evict(persistentClass);
    }

    public void evict(Class persistentClass, Serializable id) throws HibernateException
    {
        getSessionFactory().evict(persistentClass, id);
    }

    public void evictEntity(String entityName) throws HibernateException
    {
        getSessionFactory().evictEntity(entityName);
    }

    public void evictEntity(String entityName, Serializable id) throws HibernateException
    {
        getSessionFactory().evictEntity(entityName, id);
    }

    public void evictCollection(String roleName) throws HibernateException
    {
        getSessionFactory().evictCollection(roleName);
    }

    public void evictCollection(String roleName, Serializable id) throws HibernateException
    {
        getSessionFactory().evictCollection(roleName, id);
    }

    public void evictQueries() throws HibernateException
    {
        getSessionFactory().evictQueries();
    }

    public void evictQueries(String cacheRegion) throws HibernateException
    {
        getSessionFactory().evictQueries(cacheRegion);
    }

    public StatelessSession openStatelessSession()
    {
        return getSessionFactory().openStatelessSession();
    }

    public StatelessSession openStatelessSession(Connection connection)
    {
        return getSessionFactory().openStatelessSession(connection);
    }

    public Set getDefinedFilterNames()
    {
        return getSessionFactory().getDefinedFilterNames();
    }

    public Reference getReference() throws NamingException
    {
        return getSessionFactory().getReference();
    }

    public SessionFactory getSessionFactory()
    {
        SessionFactory sessionFactory = sessionFactories.get(determineCurrentSessionFactoryKey());

        if (sessionFactory == null)
        {

        }

        return sessionFactory;
    }

    protected abstract String determineCurrentSessionFactoryKey();

}
