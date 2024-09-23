package com.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/radjab";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Драйвер не найден: " + e.getMessage());
            return; // Выход из программы, если драйвер не найден
        }

        // Попытка подключения к базе данных
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (connection != null) {
                System.out.println("Подключение к базе данных успешно установлено!");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id SERIAL PRIMARY KEY, "
                + "username VARCHAR(255) UNIQUE NOT NULL, "
                + "password VARCHAR(255) NOT NULL);";

        String createLicensesTable = "CREATE TABLE IF NOT EXISTS licenses ("
                + "id SERIAL PRIMARY KEY, "
                + "key VARCHAR(255) UNIQUE NOT NULL, "
                + "user_id INTEGER REFERENCES users(id));";

        try (PreparedStatement stmt1 = connection.prepareStatement(createUsersTable);
             PreparedStatement stmt2 = connection.prepareStatement(createLicensesTable)) {
            stmt1.execute();
            stmt2.execute();
        }
    }

    private static void addUserAndLicense(Connection connection) throws SQLException {
        String insertUserSQL = "INSERT INTO users (username, password) VALUES (?, ?) RETURNING id";
        String insertLicenseSQL = "INSERT INTO licenses (key, user_id) VALUES (?, ?)";

        try (PreparedStatement userStmt = connection.prepareStatement(insertUserSQL);
             PreparedStatement licenseStmt = connection.prepareStatement(insertLicenseSQL)) {
            userStmt.setString(1, "RadjabSS");
            userStmt.setString(2, "radSS123");
            Long userId = (Long) userStmt.executeQuery().getObject(1);

            licenseStmt.setString(1, "abcd123");
            licenseStmt.setLong(2, userId);
            licenseStmt.executeUpdate();
        }
    }
}
