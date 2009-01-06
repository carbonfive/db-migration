package com.carbonfive.db.migration.maven;

import org.apache.maven.plugin.*;

/**
 * Migrate to latest schema version.
 * <p/>
 *
 * @goal migrate
 */
public class MigrateMojo extends AbstractMigrationMojo
{
    public void executeMojo() throws MojoExecutionException
    {
        getLog().info("Migrating " + getUrl() + " using migrations at " + getMigrationsPath() + ".");

        try
        {
            createMigrationManager().migrate();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to migrate " + getUrl(), e);
        }
    }
}
