package com.tourplanner.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL = "jdbc:h2:mem:tourplanner;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static boolean tablesCreated = false;

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        if (!tablesCreated) {
            createTables(connection);
            tablesCreated = true;
        }
        return connection;
    }

    private static void createTables(Connection connection) throws SQLException {
        Statement st = connection.createStatement();
        try {
            st.execute("CREATE TABLE IF NOT EXISTS tours (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "code VARCHAR(20) NOT NULL, " +
                    "title VARCHAR(100) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            st.execute("CREATE TABLE IF NOT EXISTS tour_stops (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "tour_id BIGINT NOT NULL, " +
                    "place VARCHAR(100) NOT NULL, " +
                    "stop_type VARCHAR(20) NOT NULL, " +
                    "arrival_time TIME, " +
                    "departure_time TIME, " +
                    "position INT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (tour_id) REFERENCES tours(id) ON DELETE CASCADE)");
        } finally {
            st.close();
        }
    }
}
