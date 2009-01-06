package com.carbonfive.db.migration.maven;

import com.carbonfive.db.jdbc.schema.*;
import static org.apache.commons.lang.StringUtils.*;
import org.apache.maven.plugin.*;

/**
 * @goal drop
 */
public class DropMojo extends AbstractMigrationMojo
{
    public void executeMojo() throws MojoExecutionException
    {
        getLog().info("Dropping database " + getUrl() + ".");

        try
        {
            String dropSql = isBlank(getDropSql()) ? DropDatabase.DROP_DATABASE_SQL : getDropSql();
            new DropDatabase(getDriver(), getUrl(), getUsername(), getPassword()).execute(dropSql);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to drop database " + getUrl(), e);
        }
    }
}