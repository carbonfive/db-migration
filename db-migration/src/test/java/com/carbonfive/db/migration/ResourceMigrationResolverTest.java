package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.DatabaseType;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

public class ResourceMigrationResolverTest
{
    static final String SINGLE = "classpath:/test_migrations/valid_1/";
    static final String MULTIPLE = "classpath:/test_migrations/valid_2/";

    @Test
    public void testResolveMigrationsSet1()
    {
        ResourceMigrationResolver resolver = new ResourceMigrationResolver(SINGLE);
        Set<Migration> migrations = resolver.resolve(DatabaseType.UNKNOWN);
        assertNotNull(migrations);
        assertThat(migrations, hasSize(1));
    }

    @Test
    public void testResolveMigrationsSet2()
    {
        ResourceMigrationResolver resolver = new ResourceMigrationResolver(MULTIPLE);
        Set<Migration> migrations = resolver.resolve(DatabaseType.UNKNOWN);
        assertNotNull(migrations);
        assertThat(migrations, hasSize(3));
    }

    @Test
    public void testSpringifyingMigrationsLocation()
    {
        // Append Trailing slash and wildcard.
        assertThat(convert("src/migrations"), is("file:src/migrations/*"));
        assertThat(convert("src/migrations/"), is("file:src/migrations/*"));

        // Explicit inclusion of spring prefixes.
        assertThat(convert("file:src/migrations"), is("file:src/migrations/*"));
        assertThat(convert("classpath:com/acme"), is("classpath:com/acme/*"));
        assertThat(convert("classpath:com/acme/"), is("classpath:com/acme/*"));
        assertThat(convert("classpath:/com/acme"), is("classpath:/com/acme/*"));
        //assertThat(convert("classpath*:/com/acme"), is("classpath*:/com/acme/*"));

        // Wildcards and filename filters.
        assertThat(convert("src/migrations/*.sql"), is("file:src/migrations/*.sql"));
        assertThat(convert("src/migrations/patch-*.sql"), is("file:src/migrations/patch-*.sql"));
        assertThat(convert("src/migrations/**/patch-*.sql"), is("file:src/migrations/**/patch-*.sql"));
    }

    private String convert(String location)
    {
        return new ResourceMigrationResolver("").convertMigrationsLocation(location);
    }
}
