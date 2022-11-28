package webservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger = LogManager.getLogger();

    private static final String USER_TOMCAT_DS = "java:comp/env/jdbc/userDS";

    private static final String DRIVER_CLASS = "org.apache.derby.jdbc.ClientDriver";
    public static final String JDBC_URL = "jdbc:derby://localhost:1527/UsersDb";

    public static Connection getDatabaseConnection() throws DatabaseException {
        return getConnection();
    }

    private static Connection openConnection() throws DatabaseException {
        try {
            Class.forName(DRIVER_CLASS);
            Connection connection = DriverManager.getConnection(JDBC_URL);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Unable to load database driver", e);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to connect to the database", e);
        }
    }

    private static Connection getConnection() throws DatabaseException {
        try {
            DataSource ds = getDataSource(USER_TOMCAT_DS);
            return ds.getConnection();
        } catch (NamingException e) {
            logger.error("fail in connection. look a datasource");
            throw new DatabaseException("Unable to find datasource", e);
        } catch (SQLException e) {
            logger.error("Unable to connect to the database");
            throw new DatabaseException("Unable to connect to the database", e);
        }
    }

    private static DataSource getDataSource(String jndiName) throws NamingException {
        Context initCtx = new InitialContext();
        return (DataSource) initCtx.lookup(jndiName);
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.warn("Unable to close connection", e);
            }
        }
    }
}
