package webservices.repository;

import ge.ufc.webservices.exception.PersonNotFound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRepositoryImpl implements PersonRepository {
    private static final Logger logger = LogManager.getLogger();
    private Connection connection;

    public PersonRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public String Check(int id) throws PersonNotFound {
        return getUser(id);
    }

    @Override
    public long Pay(int id, double amount) throws PersonNotFound {
        return getPay(id,amount);
    }

    private String getUser(int user_id) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Users WHERE id = ?")) {
            ps.setInt(1, user_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String lastname = rs.getString("lastname");
                    double balance = rs.getDouble("balance");
                    return CheckValue(name, lastname, balance);
                } else {
                    logger.error("User not Found");
                    throw new RuntimeException();
                }
            }
        } catch (final SQLException e) {
            logger.error("error exception");
            throw new RuntimeException();
        }
    }

    private long getPay(int userId, double amount) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO TRANSACTIONS(USER_ID, AMOUNT) VALUES (?, ?)")) {
            return getTransaction(ps, userId, amount);
        } catch (SQLException e) {
            logger.error("Error");
            throw new RuntimeException();
        }
    }

    private long getTransaction(PreparedStatement ps, int userId, double amount) throws SQLException {
        ps.setInt(1, userId);
        ps.setDouble(2, amount);
        ps.executeUpdate();
        try (PreparedStatement pr = connection.prepareStatement("UPDATE USERS SET BALANCE = BALANCE + (SELECT AMOUNT FROM TRANSACTIONS WHERE USER_ID = ?) WHERE ID = ?")) {
            pr.setInt(1, userId);
            pr.setInt(2, userId);
            pr.executeUpdate();
        }
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                logger.error("Error");
                throw new RuntimeException();
            }
        }
    }
    private String CheckValue(String name,  String surname, double balance) {
        return name.charAt(0) + " " + surname.charAt(0) + " " + balance;
    }
}
