package com.carbonfive.db.jdbc;

import com.mockrunner.mock.jdbc.MockConnection;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;

public class ScriptRunnerImplTest
{
    @Test
    public void scriptRunnerShouldBatchSimpleCommands() throws Exception
    {
        ScriptRunner runner = new ScriptRunner(DatabaseType.UNKNOWN);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("simple.sql"));
        MockConnection connection = new MockConnection();

        runner.execute(connection, reader);

        List statements = connection.getStatementResultSetHandler().getExecutedStatements();

        assertThat(statements.size(), is(2));
        assertThat(statements.get(0).toString(),
                is(equalToIgnoringWhiteSpace("create table users ( username varchar not null, password varchar not null )")));
        assertThat(statements.get(1).toString(),
                is(equalToIgnoringWhiteSpace("alter table users add index (username), add unique (username)")));
    }

    @Test
    public void scriptRunnerShouldBatchMySQLFunctionsAndProcedures() throws Exception
    {
        ScriptRunner runner = new ScriptRunner(DatabaseType.MYSQL);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("stored-procedure-mysql.sql"));
        MockConnection connection = new MockConnection();

        runner.execute(connection, reader);

        List statements = connection.getStatementResultSetHandler().getExecutedStatements();

        assertThat(statements.size(), is(3));
        assertThat(statements.get(0).toString(),
                is(equalToIgnoringWhiteSpace("CREATE FUNCTION hello (s CHAR(20)) RETURNS CHAR(50) DETERMINISTIC RETURN CONCAT('Hello, ',s,'!')")));
        assertThat(statements.get(1).toString(),
                is(equalToIgnoringWhiteSpace("CREATE FUNCTION weighted_average (n1 INT, n2 INT, n3 INT, n4 INT) RETURNS INT DETERMINISTIC BEGIN DECLARE avg INT; SET avg = (n1+n2+n3*2+n4*4)/8; RETURN avg; END")));
        assertThat(statements.get(2).toString(),
                is(equalToIgnoringWhiteSpace("CREATE PROCEDURE payment(payment_amount DECIMAL(6,2), payment_seller_id INT) BEGIN DECLARE n DECIMAL(6,2); SET n = payment_amount - 1.00; INSERT INTO Moneys VALUES (n, CURRENT_DATE); IF payment_amount > 1.00 THEN UPDATE Sellers SET commission = commission + 1.00 WHERE seller_id = payment_seller_id; END IF; END")));
    }

    @Test
    public void scriptRunnerShouldBatchPostgresFunctionsAndProcedures() throws Exception
    {
        ScriptRunner runner = new ScriptRunner(DatabaseType.POSTGRESQL);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("stored-procedure-postgresql.sql"));
        MockConnection connection = new MockConnection();

        runner.execute(connection, reader);

        List statements = connection.getStatementResultSetHandler().getExecutedStatements();

        assertThat(statements.size(), is(4));
        assertThat(statements.get(0).toString(),
                is(equalToIgnoringWhiteSpace("CREATE FUNCTION getQtyOrders(customerID int) RETURNS int AS $$ DECLARE qty int; BEGIN SELECT COUNT(*) INTO qty FROM Orders WHERE accnum = customerID; RETURN qty; END; $$ LANGUAGE plpgsql")));
        assertThat(statements.get(1).toString(),
                is(equalToIgnoringWhiteSpace("CREATE FUNCTION one() RETURNS integer AS ' SELECT 1 AS result; ' LANGUAGE SQL")));
        assertThat(statements.get(2).toString(),
                is(equalToIgnoringWhiteSpace("CREATE FUNCTION emp_stamp() RETURNS trigger AS $emp_stamp$ BEGIN IF NEW.empname IS NULL THEN RAISE EXCEPTION 'empname cannot be null'; END IF; IF NEW.salary IS NULL THEN RAISE EXCEPTION '% cannot have null salary', NEW.empname; END IF; IF NEW.salary < 0 THEN RAISE EXCEPTION '% cannot have a negative salary', NEW.empname; END IF; NEW.last_date := current_timestamp; NEW.last_user := current_user; RETURN NEW; END; $emp_stamp$ LANGUAGE plpgsql")));
        assertThat(statements.get(3).toString(),
                is(equalToIgnoringWhiteSpace("SELECT one()")));
    }

    @Test
    public void scriptRunnerShouldUseTheSameDelimiterUntilExplicitlyChanged() throws Exception
    {
        ScriptRunner runner = new ScriptRunner(DatabaseType.UNKNOWN);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("function-mysql.sql"));
        MockConnection connection = new MockConnection();

        runner.execute(connection, reader);

        List statements = connection.getStatementResultSetHandler().getExecutedStatements();

        assertThat(statements.size(), is(3));
        assertThat(statements.get(0).toString(),
                is(equalToIgnoringWhiteSpace("DROP FUNCTION IF EXISTS simpleFunction")));
        assertThat(statements.get(1).toString(),
                is(equalToIgnoringWhiteSpace("CREATE FUNCTION simpleFunction() RETURNS varchar(100) READS SQL DATA begin declare message varchar(100) default 'Hello Word'; return message; end")));
        assertThat(statements.get(2).toString(),
                is(equalToIgnoringWhiteSpace("select simpleFunction()")));
    }

    @Test
    public void scriptRunnerShouldExecuteLastStatementWhenDelimiterIsMissing() throws Exception
    {
        ScriptRunner runner = new ScriptRunner(DatabaseType.UNKNOWN);
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("missing-last-deliminator.sql"));
        MockConnection connection = new MockConnection();

        runner.execute(connection, reader);

        List statements = connection.getStatementResultSetHandler().getExecutedStatements();

        assertThat(statements.size(), is(2));
        assertThat(statements.get(0).toString(),
                is(equalToIgnoringWhiteSpace("create table users ( username varchar not null, password varchar not null )")));
        assertThat(statements.get(1).toString(),
                is(equalToIgnoringWhiteSpace("create table roles ( name varchar not null unique, description text not null )")));
    }
}
