package com.carbonfive.db.migration;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

public class ResourceMigrationResolverTest
{
    static final String SINGLE = "classpath:/test_migrations/valid_1/";
    static final String MULTIPLE = "classpath:/test_migrations/valid_2/";

    @Test
    public void testResolveMigrationsSet1()
    {
        ResourceMigrationResolver resolver = new ResourceMigrationResolver(SINGLE);
        Set<Migration> migrations = resolver.resolve();
        assertNotNull(migrations);
        assertThat(migrations.size(), is(1));
    }

    @Test
    public void testResolveMigrationsSet2()
    {
        ResourceMigrationResolver resolver = new ResourceMigrationResolver(MULTIPLE);
        Set<Migration> migrations = resolver.resolve();
        assertNotNull(migrations);
        assertThat(migrations.size(), is(3));
    }
}
