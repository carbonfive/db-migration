package com.carbonfive.db.hibernate.repository;


import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserCollectionType;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractHibernateRepository<T, Id extends Serializable> {
    private Class<T> persistentClass;

    private SessionFactory sessionFactory;

    public AbstractHibernateRepository() {
        this.persistentClass = (Class<T>) ((UserCollectionType) getClass().getGenericSuperclass()).getCollectionClass();
    }

    protected AbstractHibernateRepository(SessionFactory sessionFactory) {
        this();
        this.sessionFactory = sessionFactory;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void flushSession() {
        getSession().flush();
    }

    public void clearSession() {
        getSession().clear();
    }

    public T findById(Id id, boolean lock) {
        T entity;

        if (lock) {
            entity = (T) getSession().get(getPersistentClass(), id, LockMode.UPGRADE_SKIPLOCKED);
        } else {
            entity = (T) getSession().get(getPersistentClass(), id);
        }

        return entity;
    }

    public List<T> findAll() {
        CriteriaQuery criteria = getSession()
                .getEntityManagerFactory()
                .createEntityManager()
                .getCriteriaBuilder()
                .createQuery(persistentClass);
        return criteria.getOrderList();
    }

    public T makePersistent(T entity) {
        getSession().saveOrUpdate(entity);
        return entity;
    }

    public void makeTransient(T entity) {
        getSession().delete(entity);
    }

    public void makeTransient(Id id) {
        Object entity = getSession().get(getPersistentClass(), id);
        getSession().delete(entity);
    }

    public void refresh(T entity) {
        getSession().refresh(entity);
    }

    public T merge(T entity) {
        return (T) getSession().merge(entity);
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
