package com.carbonfive.db.migration;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;

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
