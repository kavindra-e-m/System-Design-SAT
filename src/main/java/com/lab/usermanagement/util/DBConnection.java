package com.lab.usermanagement.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to manage database connections using JDBC.
 * Reads configuration from db.properties, system properties, or environment variables.
 * Ensures a fresh connection is provided without maintaining a unsafe global shared connection.
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    private static String jdbcUrl = "jdbc:mysql://localhost:3306/user_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String jdbcUser = "root";
    private static String jdbcPassword = "";

    static {
        loadConfiguration();
        try {
            Class.forName(jdbcDriver);
            LOGGER.info("JDBC Driver successfully registered: " + jdbcDriver);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load JDBC driver: " + jdbcDriver, e);
        }
    }

    /**
     * Loads database credentials from classpath properties file,
     * environment variables, and system properties (in increasing order of precedence).
     */
    public static synchronized void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                if (props.getProperty("db.driver") != null) jdbcDriver = props.getProperty("db.driver").trim();
                if (props.getProperty("db.url") != null) jdbcUrl = props.getProperty("db.url").trim();
                if (props.getProperty("db.user") != null) jdbcUser = props.getProperty("db.user").trim();
                if (props.getProperty("db.password") != null) jdbcPassword = props.getProperty("db.password").trim();
                LOGGER.info("Loaded database configuration from classpath 'db.properties'");
            } else {
                LOGGER.warning("db.properties not found on classpath; using default configuration");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error reading db.properties: " + e.getMessage(), e);
        }

        // Allow Environment Variables override
        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.isBlank()) jdbcUrl = envUrl;

        String envUser = System.getenv("DB_USER");
        if (envUser != null && !envUser.isBlank()) jdbcUser = envUser;

        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null) jdbcPassword = envPass;

        // Allow JVM System Properties override (e.g. -Ddb.password=secret)
        String sysDriver = System.getProperty("db.driver");
        if (sysDriver != null && !sysDriver.isBlank()) jdbcDriver = sysDriver;

        String sysUrl = System.getProperty("db.url");
        if (sysUrl != null && !sysUrl.isBlank()) jdbcUrl = sysUrl;

        String sysUser = System.getProperty("db.user");
        if (sysUser != null && !sysUser.isBlank()) jdbcUser = sysUser;

        String sysPass = System.getProperty("db.password");
        if (sysPass != null) jdbcPassword = sysPass;
    }

    /**
     * Retrieves a new active Connection to the database.
     * The caller is responsible for closing this connection (ideally with try-with-resources).
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failure to: " + jdbcUrl + " as user: " + jdbcUser, e);
            throw e;
        }
    }

    /**
     * Helper to test if database connection is currently alive and responsive.
     *
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static String getJdbcUser() {
        return jdbcUser;
    }

    /**
     * Allows test classes or runners to set custom connection details dynamically.
     */
    public static synchronized void configure(String driver, String url, String user, String password) {
        jdbcDriver = driver;
        jdbcUrl = url;
        jdbcUser = user;
        jdbcPassword = password;
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not load driver: " + driver, e);
        }
    }
}
