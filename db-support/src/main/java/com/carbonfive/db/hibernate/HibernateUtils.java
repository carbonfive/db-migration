package com.carbonfive.db.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

public class HibernateUtils {
    public static void clearSecondLevelCache(SessionFactory sessionFactory) throws HibernateException {
        sessionFactory.getCache().evictEntityData();
        sessionFactory.getCache().evictAll();
        sessionFactory.getCache().evictAllRegions();
        sessionFactory.getCache().evictQueryRegions();
    }
}
