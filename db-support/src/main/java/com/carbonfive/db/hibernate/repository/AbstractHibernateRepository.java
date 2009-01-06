package com.carbonfive.db.hibernate.repository;

import org.hibernate.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public abstract class AbstractHibernateRepository<T, Id extends Serializable>
{
    private Class<T> persistentClass;

    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public AbstractHibernateRepository()
    {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected AbstractHibernateRepository(SessionFactory sessionFactory)
    {
        this();
        this.sessionFactory = sessionFactory;
    }

    public Class<T> getPersistentClass()
    {
        return persistentClass;
    }

    public void flushSession()
    {
        getSession().flush();
    }

    public void clearSession()
    {
        getSession().clear();
    }

    @SuppressWarnings("unchecked")
    public T findById(Id id, boolean lock)
    {
        T entity;

        if (lock)
        {
            entity = (T) getSession().get(getPersistentClass(), id, LockMode.UPGRADE);
        }
        else
        {
            entity = (T) getSession().get(getPersistentClass(), id);
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll()
    {
        Criteria criteria = getSession().createCriteria(persistentClass);
        return criteria.list();
    }

    public T makePersistent(T entity)
    {
        getSession().saveOrUpdate(entity);
        return entity;
    }

    public void makeTransient(T entity)
    {
        getSession().delete(entity);
    }

    public void makeTransient(Id id)
    {
        Object entity = getSession().get(getPersistentClass(), id);
        getSession().delete(entity);
    }

    public void refresh(T entity)
    {
        getSession().refresh(entity);
    }

    @SuppressWarnings("unchecked")
    public T merge(T entity)
    {
        return (T) getSession().merge(entity);
    }

    protected Session getSession()
    {
        return sessionFactory.getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }
}
