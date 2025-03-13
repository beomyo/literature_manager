package com.example.utils.txt2mysql;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/mydatabase?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private static Set<String> existTitleAuthorCombinations = new HashSet<>();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTableIfNotExists() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS artical_info ("
                    + "`Id` INT AUTO_INCREMENT PRIMARY KEY, "
                    + "`SrcDatabase` VARCHAR(255), "
                    + "`Title` VARCHAR(255), "
                    + "`Author` VARCHAR(255), "
                    + "`Organ` VARCHAR(255), "
                    + "`Source` VARCHAR(255), "
                    + "`Keyword` VARCHAR(255), "
                    + "`Summary` TEXT, "
                    + "`PubTime` VARCHAR(255), "
                    + "`FirstDuty` VARCHAR(255), "
                    + "`Fund` TEXT, "
                    + "`Year` VARCHAR(255), "
                    + "`PageCount` VARCHAR(255), "
                    + "`CLC` VARCHAR(255), "
                    + "`URL` TEXT, "
                    + "`DOI` VARCHAR(255), "
                    + "UNIQUE (Title, Author)"
                    + ")";
            stmt.execute(sql);
        }
    }

    public static void loadExistTitleAuthorCombinations() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT Title, Author FROM article_info")) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String combination = rs.getString("Title") + "-" + rs.getString("Author");
                existTitleAuthorCombinations.add(combination);
            }
        }
    }

    public static void insertRecord(Record record) throws SQLException {
        if (record.getTitle() == null || record.getTitle().isEmpty()) {
            System.out.println("Title is missing or empty for record: " + record.getSrcDatabase());
            return;
        }
        if (record.getAuthor() == null || record.getAuthor().isEmpty()) {
            System.out.println("Author is missing or empty for record: " + record.getTitle());
            return;
        }

        String combination = record.getTitle() + "-" + record.getAuthor();
        if (existTitleAuthorCombinations.contains(combination)) {
            System.out.println("Record with Title '" + record.getTitle() + "' and Author '" + record.getAuthor() + "' already exists. Skipping...");
            return;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO article_info (`SrcDatabase`, `Title`, `Author`, `Organ`, "
                             + "`Source`, `Keyword`, `Summary`, `PubTime`, "
                             + "`FirstDuty`, `Fund`, `Year`, `PageCount`, "
                             + "`CLC`, `URL`, `DOI`) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setString(1, record.getSrcDatabase());
            pstmt.setString(2, record.getTitle());
            pstmt.setString(3, record.getAuthor());
            pstmt.setString(4, record.getOrgan());
            pstmt.setString(5, record.getSource());
            pstmt.setString(6, record.getKeyword());
            pstmt.setString(7, record.getSummary());
            pstmt.setString(8, record.getPubTime());
            pstmt.setString(9, record.getFirstDuty());
            pstmt.setString(10, record.getFund());
            pstmt.setString(11, record.getYear());
            pstmt.setString(12, record.getPageCount());
            pstmt.setString(13, record.getCLC());
            pstmt.setString(14, record.getURL());
            pstmt.setString(15, record.getDOI());

            pstmt.executeUpdate();
            existTitleAuthorCombinations.add(combination);
            System.out.println("Record inserted successfully: " + record.getTitle() + " by " + record.getAuthor());
        } catch (SQLException e) {
            System.err.println("Failed to insert record: " + e.getMessage());
        }
    }
}