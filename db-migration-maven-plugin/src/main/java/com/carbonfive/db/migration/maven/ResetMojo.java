package com.carbonfive.db.migration.maven;

import com.carbonfive.db.jdbc.schema.*;
import static org.apache.commons.lang.StringUtils.*;
import org.apache.maven.plugin.*;

import java.sql.*;

/**
 * @goal reset
 */
public class ResetMojo extends AbstractMigrationMojo
{
    public void executeMojo() throws MojoExecutionException
    {
        getLog().info("Resetting database " + getUrl() + ".");

        try
        {
            getLog().info("Dropping database " + getUrl() + ".");
            String dropSql = isBlank(getDropSql()) ? DropDatabase.DROP_DATABASE_SQL : getDropSql();
            new DropDatabase(getDriver(), getUrl(), getUsername(), getPassword()).execute(dropSql);
        }
        catch (ClassNotFoundException e)
        {
            throw new MojoExecutionException("Failed to reset database " + getUrl(), e);
        }
        catch (SQLException ignored)
        {
        }

        try
        {
            getLog().info("Creating database " + getUrl() + ".");
            String createSql = isBlank(getCreateSql()) ? CreateDatabase.CREATE_DATABASE_SQL : getCreateSql();
            new CreateDatabase(getDriver(), getUrl(), getUsername(), getPassword()).execute(createSql);

            getLog().info("Migrating database " + getUrl() + ".");
            createMigrationManager().migrate();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to reset database " + getUrl(), e);
        }
    }
}