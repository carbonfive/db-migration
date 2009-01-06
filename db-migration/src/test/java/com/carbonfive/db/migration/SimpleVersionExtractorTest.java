package com.carbonfive.db.migration;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.*;

public class SimpleVersionExtractorTest
{
    @Test(expected = MigrationException.class)
    public void testInvalidFilename()
    {
        new SimpleVersionExtractor().extractVersion("foo.sql");
    }

    @Test
    public void testNumberParsing()
    {
        VersionExtractor extractor = new SimpleVersionExtractor();
        assertThat(extractor.extractVersion("000.sql"), is("000"));
        assertThat(extractor.extractVersion("000_create_foo.sql"), is("000"));
        assertThat(extractor.extractVersion("20080718214030_tinman.sql"), is("20080718214030"));
    }
}
