package com.library.Library1;

import java.sql.*;
import java.util.Scanner;

public class Library {

    private static final String Url = "jdbc:postgresql://localhost:5432/UniversityDB"; 
    private static final String User = "postgres";  
    private static final String Password = "root";

    private static Connection conn;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(Url, User, Password);
            System.out.println("Connected to Database!");

            int choice;
            do {
                System.out.println("\n Library Management System ");
                System.out.println("1. Add Book");
                System.out.println("2. View All Books");
                System.out.println("3. Search Book");
                System.out.println("4. Issue Book");
                System.out.println("5. Return Book");
                System.out.println("6. Delete Book");
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        viewAllBooks();
                        break;
                    case 3:
                        searchBook();
                        break;
                    case 4:
                        issueBook();
                        break;
                    case 5:
                        returnBook();
                        break;
                    case 6:
                        deleteBook();
                        break;
                    case 7:
                        System.out.println("Exiting... Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } while (choice != 7);

            conn.close();
            scanner.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addBook() throws SQLException {
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter Available Copies: ");
        int copies = scanner.nextInt();

        String sql = "INSERT INTO Library (title, author, price, available_copies) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, author);
        ps.setDouble(3, price);
        ps.setInt(4, copies);

        int rows = ps.executeUpdate();
        System.out.println(rows + " book(s) added successfully.");
    }

    private static void viewAllBooks() throws SQLException {
        String sql = "SELECT * FROM Library";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("\n--- Book List ---");
        while (rs.next()) {
            int id = rs.getInt("book_id");
            String title = rs.getString("title");
            String author = rs.getString("author");
            double price = rs.getDouble("price");
            int copies = rs.getInt("available_copies");

            System.out.println(id + " | " + title + " | " + author + " | " + price + " | " + copies);
        }
    }

    private static void searchBook() throws SQLException {
        System.out.print("Enter keyword (title/author): ");
        String keyword = scanner.nextLine();

        String sql = "SELECT * FROM Library WHERE title ILIKE ? OR author ILIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");

        ResultSet rs = ps.executeQuery();
        System.out.println("\n--- Search Results ---");
        while (rs.next()) {
            int id = rs.getInt("book_id");
            String title = rs.getString("title");
            String author = rs.getString("author");
            double price = rs.getDouble("price");
            int copies = rs.getInt("available_copies");

            System.out.printf("ID: %d | Title: %s | Author: %s | Price: %.2f | Copies: %d%n",
                    id, title, author, price, copies);
        }
    }

    private static void issueBook() throws SQLException {
        System.out.print("Enter Book ID to Issue: ");
        int id = scanner.nextInt();

        String checkSql = "SELECT available_copies FROM Library WHERE book_id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setInt(1, id);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            int copies = rs.getInt("available_copies");
            if (copies > 0) {
                String updateSql = "UPDATE Library SET available_copies = available_copies - 1 WHERE book_id = ?";
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setInt(1, id);
                ps.executeUpdate();
                System.out.println("Book issued successfully!");
            } else {
                System.out.println("No copies available!");
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void returnBook() throws SQLException {
        System.out.print("Enter Book ID to Return: ");
        int id = scanner.nextInt();

        String updateSql = "UPDATE Library SET available_copies = available_copies + 1 WHERE book_id = ?"; 
        PreparedStatement ps = conn.prepareStatement(updateSql);
        ps.setInt(1, id);

        int rows = ps.executeUpdate();
        if (rows > 0) {
            System.out.println("Book returned successfully!");
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void deleteBook() throws SQLException {
        System.out.print("Enter Book ID to Delete: ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM Library WHERE book_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int rows = ps.executeUpdate();
        if (rows > 0) {
            System.out.println("Book deleted successfully.");
        } else {
            System.out.println("Book not found.");
        }
    }
}
