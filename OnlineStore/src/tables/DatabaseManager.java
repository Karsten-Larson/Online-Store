package tables;

import java.sql.*;

/**
 * Class for managing database connection throughout the project
 *
 * @author karsten
 */
class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/onlinestore";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "password";

    private static Connection connection;

    /**
     * Gets the universal database connection for the project
     *
     * @return database connection
     */
    public static Connection getConnection() {
        if (connection == null) {
            setConnection();
        }

        return connection;
    }

    /**
     * Connects to the universal database for the project. Provides the
     * connection its URL, username, and password credentials
     */
    private static void setConnection() {
        try {
            // load the driver
            Class.forName("org.postgresql.Driver");
            // connect to the database
            connection = 
                    DriverManager.getConnection(URL, USERNAME, PASSWORD);
           
        }
        catch (ClassNotFoundException e) {
            System.out.println("Cannot load the driver");
            e.printStackTrace();
        }
        catch (SQLException e) {
            System.out.println("Got a SQL exception.");
            e.printStackTrace();
        }
    }
}
