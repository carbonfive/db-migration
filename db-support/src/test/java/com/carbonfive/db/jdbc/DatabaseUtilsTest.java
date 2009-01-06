package com.carbonfive.db.jdbc;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import org.junit.*;

public class DatabaseUtilsTest
{
    @Test
    public void extractDatabaseName()
    {
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql:dbname"), is("dbname"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://localhost/dbname"), is("dbname"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://127.0.0.1/dbname"), is("dbname"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://pants/a_b_c"), is("a_b_c"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://pants/a-b-c"), is("a-b-c"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://localhost:3306/dbname"), is("dbname"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://127.0.0.1:3306/dbname"), is("dbname"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://localhost/dbname;OPTION1=A;OPTION2=B"), is("dbname"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://127.0.0.1:3306/dbname;OPTION1=A;OPTION2=B"), is("dbname"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://localhost/dbname?OPTION1=A&OPTION2=B"), is("dbname"));
        assertThat(DatabaseUtils.extractDatabaseName("jdbc:mysql://127.0.0.1:3306/dbname?OPTION1=A&OPTION2=B"), is("dbname"));
    }

    @Test
    public void extractServerUrl()
    {
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql:dbname"), is("jdbc:mysql"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://localhost/dbname"), is("jdbc:mysql://localhost"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://pants/a_b_c"), is("jdbc:mysql://pants"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://pants/a-b-c"), is("jdbc:mysql://pants"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://localhost/dbname"), is("jdbc:mysql://localhost"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://localhost:3306/dbname"), is("jdbc:mysql://localhost:3306"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://127.0.0.1:3306/dbname"), is("jdbc:mysql://127.0.0.1:3306"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://localhost/dbname;OPTION1=A;OPTION2=B"), is("jdbc:mysql://localhost;OPTION1=A;OPTION2=B"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://127.0.0.1:3306/dbname;OPTION1=A;OPTION2=B"),
                   is("jdbc:mysql://127.0.0.1:3306;OPTION1=A;OPTION2=B"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://localhost/dbname?OPTION1=A&OPTION2=B"), is("jdbc:mysql://localhost?OPTION1=A&OPTION2=B"));
        assertThat(DatabaseUtils.extractServerUrl("jdbc:mysql://127.0.0.1:3306/dbname?OPTION1=A&OPTION2=B"),
                   is("jdbc:mysql://127.0.0.1:3306?OPTION1=A&OPTION2=B"));
    }
}
