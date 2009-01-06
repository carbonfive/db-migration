package com.carbonfive.db.migration.maven;

import org.apache.maven.plugin.testing.*;

import java.io.*;

public class MigrateMojoTest extends AbstractMojoTestCase
{
    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void testConfiguration() throws Exception
    {
        File pom = new File(getBasedir(), "src/test/test-project/pom.xml");

        MigrateMojo mojo = (MigrateMojo) lookupMojo("migrate", pom);
        assertNotNull(mojo);

        assertEquals("org.h2.Driver", mojo.getDriver());
        assertEquals("jdbc:h2:mem:maven-migration-plugin;DB_CLOSE_DELAY=-1", mojo.getUrl());
        assertEquals("root", mojo.getUsername());
        assertEquals("", mojo.getPassword());
        assertEquals("src/main/db/migrations/", mojo.getMigrationsPath());
    }
}
