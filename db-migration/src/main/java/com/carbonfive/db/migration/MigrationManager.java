package com.carbonfive.db.migration;

import java.util.*;

public interface MigrationManager
{
    /**
     * Validates whether the database is currently up-to-date.
     *
     * @return true if the database is up-to-date, false if it is not or is unversioned
     */
    boolean validate();

    /**
     * Returns a sorted set of pending migration versions, in the order that they would be run if a migration was performed.
     *
     * @return a sorted set of pending migration versions, or an empty set if there are none pending
     */
    SortedSet<String> pendingMigrations();

    /**
     * Migrates the database to the latest version, enabling migrations if necessary.
     */
    void migrate();
}
