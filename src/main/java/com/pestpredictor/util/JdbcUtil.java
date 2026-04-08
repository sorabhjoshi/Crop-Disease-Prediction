package com.pestpredictor.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for direct JDBC connections.
 * Used alongside Hibernate for raw SQL operations when needed.
 */
public class JdbcUtil {

    private static final Logger logger = Logger.getLogger(JdbcUtil.class.getName());

    // These should match application.properties
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pest_predictor" +
            "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER_CLASS);
            logger.info("MySQL JDBC Driver loaded.");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private JdbcUtil() {}

    /**
     * Returns a new JDBC connection from DriverManager.
     * Caller is responsible for closing the connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Closes a connection quietly, suppressing exceptions.
     */
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Failed to close JDBC connection", e);
            }
        }
    }

    /**
     * Tests whether a connection to the database can be established.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection test failed", e);
            return false;
        }
    }
}
