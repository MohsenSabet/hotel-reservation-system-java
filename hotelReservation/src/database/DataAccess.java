package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataAccess {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/hotelreservation";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "6ea2df";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
    }
}
