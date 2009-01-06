package com.carbonfive.db.migration;

import com.carbonfive.db.jdbc.*;

import java.sql.*;

public interface Migration extends Comparable<Migration>
{
    String getVersion();

    void migrate(DatabaseType dbType, Connection connection);
}
