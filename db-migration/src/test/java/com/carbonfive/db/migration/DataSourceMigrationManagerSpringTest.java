package com.carbonfive.db.migration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Note: This is really more of an example of how to wire up using spring configuration than an actual test.  See the corresponding spring xml to see one way
 * migrations can be configured.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DataSourceMigrationManagerSpringTest
{
    @Autowired MigrationManager migrationManager;

    @Test
    public void testMigration()
    {
        migrationManager.migrate();
        assertThat(migrationManager.validate(), is(true));
    }
}
