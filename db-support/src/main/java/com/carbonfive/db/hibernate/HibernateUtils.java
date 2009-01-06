package com.carbonfive.db.hibernate;

import org.hibernate.*;

import java.util.*;

public class HibernateUtils
{
    public static void clearSecondLevelCache(SessionFactory sessionFactory) throws HibernateException
    {
        for (String clazz : (Set<String>) sessionFactory.getAllClassMetadata().keySet())
        {
            sessionFactory.evictEntity(clazz);
        }
        for (String rolename : (Set<String>) sessionFactory.getAllCollectionMetadata().keySet())
        {
            sessionFactory.evictCollection(rolename);
        }
        sessionFactory.evictQueries();
    }
}
