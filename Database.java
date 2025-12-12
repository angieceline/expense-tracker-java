package com.celine.expensetracker;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:expenses.db";

    static {
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {
            String create = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "type TEXT NOT NULL," +
                    "amount TEXT NOT NULL," +
                    "date TEXT NOT NULL," +
                    "category TEXT," +
                    "note TEXT" +
                    ")";
            s.execute(create);
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
