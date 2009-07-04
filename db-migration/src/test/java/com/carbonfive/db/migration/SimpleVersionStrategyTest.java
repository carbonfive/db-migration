package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.DatabaseType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

public class SimpleVersionStrategyTest
{
    private static final String TABLE_NAME = "db_version";
    private static final String VERSION_COLUMN = "currentVersion";

    private SimpleVersionStrategy strategy;
    private DataSource dataSource;

    @Before
    public void setup()
    {
        dataSource = DatabaseTestUtils.createUniqueDataSource();

        strategy = new SimpleVersionStrategy();
        strategy.setVersionTable(TABLE_NAME);
        strategy.setVersionColumn(VERSION_COLUMN);
    }

    @Test
    public void testEnableVersioning() throws SQLException
    {
        Connection connection = dataSource.getConnection();
        strategy.enableVersioning(DatabaseType.H2, connection);
        connection.close();

        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        jdbcTemplate.queryForInt("select count(*)" + VERSION_COLUMN + " from " + TABLE_NAME); // Throws exception is table doesn't exist.
    }

    @Test
    public void testDetermineVersionInUnversionedDatabase() throws SQLException
    {
        Connection connection = dataSource.getConnection();
        Set<String> migrations = strategy.appliedMigrations(DatabaseType.H2, connection);
        connection.close();

        assertThat(migrations, is(nullValue()));
    }

    @Test
    public void testDetermineVersionInNewlyEnabledDatabase() throws SQLException
    {
        Connection connection = dataSource.getConnection();
        strategy.enableVersioning(DatabaseType.H2, connection);
        Set<String> migrations = strategy.appliedMigrations(DatabaseType.H2, connection);
        connection.close();

        assertThat(migrations, not(nullValue()));
        assertThat(migrations.isEmpty(), is(true));
    }

    @Test
    public void testRecordMigration() throws SQLException
    {
        final String v1 = "20080718214030";
        final String v2 = "20080718214530";

        Connection connection = dataSource.getConnection();
        strategy.enableVersioning(DatabaseType.H2, connection);
        strategy.recordMigration(DatabaseType.H2, connection, v1, new Date(), 768);
        connection.close();

        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        assertThat(jdbcTemplate.queryForInt("select count(*) from " + TABLE_NAME), is(1));
        assertThat(jdbcTemplate.queryForObject("select " + VERSION_COLUMN + " from " + TABLE_NAME, String.class), is(v1));

        connection = dataSource.getConnection();
        strategy.recordMigration(DatabaseType.H2, connection, v2, new Date(), 231);
        connection.close();

        assertThat(jdbcTemplate.queryForInt("select count(*) from " + TABLE_NAME), is(2));

        connection = dataSource.getConnection();
        Set<String> appliedMigrations = strategy.appliedMigrations(DatabaseType.H2, connection);
        assertThat(appliedMigrations, hasSize(2));
        assertThat(appliedMigrations, hasItems(v1, v2));
        connection.close();
    }
}
