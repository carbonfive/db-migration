package com.carbonfive.db.migration.maven;

import com.carbonfive.db.migration.*;
import org.apache.maven.plugin.*;

import java.util.*;

/**
 * Validate current schema against available migrations.
 * <p/>
 *
 * @goal validate
 */
public class ValidateMojo extends AbstractMigrationMojo
{
    public void executeMojo() throws MojoExecutionException
    {
        getLog().info("Validating " + getUrl() + " using migrations at " + getMigrationsPath() + ".");

        try
        {
            MigrationManager manager = createMigrationManager();
            SortedSet<String> pendingMigrations = manager.pendingMigrations();
            getLog().info("\n             Database: " + getUrl() +
                          "\n           Up-to-date: " + pendingMigrations.isEmpty() +
                          "\n   Pending Migrations: " + pendingMigrations);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to validate " + getUrl(), e);
        }
    }
}
