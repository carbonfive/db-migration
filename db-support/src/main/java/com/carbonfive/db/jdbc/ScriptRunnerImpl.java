package com.carbonfive.db.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.*;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

/**
 * Tool to run database scripts, copied from iBatis 2.3.0 and heavily modified.
 */
class ScriptRunnerImpl
{
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String DEFAULT_DELIMITER = ";";

    public void execute(Connection connection, Reader reader) throws IOException, SQLException
    {
        try
        {
            final boolean originalAutoCommit = connection.getAutoCommit();
            try
            {
                if (originalAutoCommit)
                {
                    connection.setAutoCommit(false);
                }
                doExecute(connection, reader);
            }
            finally
            {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        }
    }

    private void doExecute(Connection conn, Reader reader) throws IOException, SQLException
    {
        StringBuffer command = null;
        try
        {
            String delimiter = DEFAULT_DELIMITER;
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null)
            {
                if (command == null)
                {
                    command = new StringBuffer();
                }

                String trimmedLine = line.trim(); // Strip extra whitespace too?

                if (trimmedLine.length() < 1)
                {
                    // Do nothing, it's an empty line.
                }
                else if (trimmedLine.startsWith("--") || trimmedLine.startsWith("#") || trimmedLine.startsWith("//"))
                {
                    logger.debug(trimmedLine);
                }
                else if (startsWithIgnoreCase(trimmedLine, "DELIMITER"))
                {
                    delimiter = trimmedLine.substring(10).trim();
                }
                else
                {
                    if (trimmedLine.endsWith(delimiter))
                    {
                        command.append(line.substring(0, line.lastIndexOf(delimiter)));
                        executeStatement(conn, command.toString());
                        command = null;
                    }
                    else
                    {
                        command.append(line);
                        command.append(" ");
                    }
                }
            }

            // Check to see if we have an unexecuted statement in command.
            if (command != null && command.length() > 0)
            {
                logger.info("Last statement in script is missing a terminating delimiter, executing anyway.");
                executeStatement(conn, command.toString());
            }
        }
        catch (SQLException e)
        {
            e.fillInStackTrace();
            logger.error("Error executing: " + command, e);
            throw e;
        }
        catch (IOException e)
        {
            e.fillInStackTrace();
            logger.error("Error executing: " + command, e);
            throw e;
        }
    }

    private void executeStatement(Connection conn, String command) throws SQLException
    {
        Statement statement = conn.createStatement();

        logger.debug(command);

        boolean hasResults = statement.execute(command);

        ResultSet rs = statement.getResultSet();

        if (hasResults && rs != null)
        {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            for (int i = 1; i <= cols; i++)
            {
                String name = md.getColumnName(i);
                logger.debug(name + "\t");
            }
            while (rs.next())
            {
                for (int i = 1; i <= cols; i++)
                {
                    String value = rs.getString(i);
                    logger.debug(value + "\t");
                }
            }
        }

        try
        {
            statement.close();
        }
        catch (Exception e)
        {
            // Ignore to workaround a bug in Jakarta DBCP
        }
        Thread.yield();
    }
}
