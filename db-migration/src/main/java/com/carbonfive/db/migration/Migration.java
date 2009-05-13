package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.DatabaseType;

import java.sql.Connection;

public interface Migration extends Comparable<Migration>
{
    String getVersion();

    String getFilename();

    void migrate(DatabaseType dbType, Connection connection);
}
