package com.carbonfive.db.migration;

import org.apache.commons.lang.*;
import org.slf4j.*;

public abstract class AbstractMigration implements Migration
{
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final String version;

    protected AbstractMigration(String version)
    {
        Validate.notNull(version);
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

    public int compareTo(Migration o)
    {
        return getVersion().compareTo(o.getVersion());
    }
}
