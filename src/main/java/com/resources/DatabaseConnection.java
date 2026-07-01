package com.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static Connection connection = null;
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;
    
    static {
        String dbUrl, dbUser, dbPassword;
        
        String railwayUrl = System.getenv("DATABASE_URL");
        String railwayUser = System.getenv("DATABASE_USERNAME");
        String railwayPass = System.getenv("DATABASE_PASSWORD");
        
        if (railwayUrl != null && !railwayUrl.isEmpty()) {
            System.out.println("✅ Using Railway Database");
            
            if (railwayUrl.startsWith("mysql://")) {
                dbUrl = railwayUrl.replace("mysql://", "jdbc:mysql://");
            } else if (railwayUrl.startsWith("postgresql://")) {
                dbUrl = railwayUrl.replace("postgresql://", "jdbc:postgresql://");
            } else {
                dbUrl = railwayUrl;
            }
            
            dbUser = railwayUser != null ? railwayUser : "";
            dbPassword = railwayPass != null ? railwayPass : "";
            
        } else {
            System.out.println("✅ Using Local MySQL");
            String host = System.getenv("MYSQLHOST") != null ? System.getenv("MYSQLHOST") : "localhost";
            String port = System.getenv("MYSQLPORT") != null ? System.getenv("MYSQLPORT") : "3306";
            String database = System.getenv("MYSQLDATABASE") != null ? System.getenv("MYSQLDATABASE") : "laundry_db";
            String user = System.getenv("MYSQLUSER") != null ? System.getenv("MYSQLUSER") : "root";
            String password = System.getenv("MYSQLPASSWORD") != null ? System.getenv("MYSQLPASSWORD") : "";
            
            dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            dbUser = user;
            dbPassword = password;
        }
        
        URL = dbUrl;
        USER = dbUser;
        PASSWORD = dbPassword;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver loaded.");
            System.out.println("✅ Database URL: " + URL);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found: " + e.getMessage());
        }
    }
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("✅ Database connection closed.");
            } catch (SQLException e) {
                System.err.println("❌ Error closing connection: " + e.getMessage());
            }
        }
    }
}