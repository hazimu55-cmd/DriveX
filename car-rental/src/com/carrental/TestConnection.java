package com.carrental;

import com.carrental.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {

    public static void main(String[] args) {
        System.out.println("Testing MySQL connection...");
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connection successful!");
                
                // Test query to check database
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
                
                if (rs.next()) {
                    System.out.println("✅ Connected to database: " + rs.getString(1));
                }
                
                // Check tables
                rs = stmt.executeQuery("SHOW TABLES");
                System.out.println("✅ Tables in drivex:");
                while (rs.next()) {
                    System.out.println("   - " + rs.getString(1));
                }
                
                DatabaseConnection.closeConnection();
                System.out.println("✅ Connection test completed successfully!");
                
            } else {
                System.out.println("❌ Connection failed");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
