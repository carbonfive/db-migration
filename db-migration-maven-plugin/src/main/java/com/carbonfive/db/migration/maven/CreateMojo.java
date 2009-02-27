package com.carbonfive.db.migration.maven;

import com.carbonfive.db.jdbc.schema.*;
import static org.apache.commons.lang.StringUtils.*;
import org.apache.maven.plugin.*;

/**
 * @goal create
 */
public class CreateMojo extends AbstractMigrationMojo
{
    public void executeMojo() throws MojoExecutionException
    {
        getLog().info("Creating database " + getUrl() + ".");

        try
        {
            String createSql = isBlank(getCreateSql()) ? CreateDatabase.CREATE_DATABASE_SQL : getCreateSql();
            new CreateDatabase(getDriver(), getUrl(), getUsername(), getPassword()).execute(createSql);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to create database " + getUrl(), e);
        }
    }
}
