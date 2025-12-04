import java.sql.*;
import java.util.Scanner;

public class AdminClient {
    private static final String URL = "jdbc:mysql://localhost:3306/cafe_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Heavyd1rtyso/ul"; // поменяй на свой

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("=== Smart Cafe Admin ===");

            boolean running = true;
            while (running) {
                printMenu();
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> viewMenu(conn);
                    case 2 -> addMenuItem(conn, sc);
                    case 3 -> deleteMenuItem(conn, sc);
                    case 4 -> viewOrders(conn);
                    case 5 -> deleteOrder(conn, sc);
                    case 6 -> running = false;
                    default -> System.out.println("Invalid choice!\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\nAdmin Options:");
        System.out.println("1. View Menu");
        System.out.println("2. Add New Menu Item");
        System.out.println("3. Delete Menu Item");
        System.out.println("4. View Orders");
        System.out.println("5. Delete Order");
        System.out.println("6. Exit");
        System.out.print("Enter choice: ");
    }

    private static void viewMenu(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items");
        System.out.println("\nID  Name             Category   Price");
        System.out.println("-------------------------------------");
        while (rs.next()) {
            System.out.printf("%-3d %-16s %-10s %.2f%n",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"));
        }
        rs.close();
        stmt.close();
    }

    private static void addMenuItem(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter item name: ");
        String name = sc.nextLine();
        System.out.print("Enter category (sweet/drink): ");
        String category = sc.nextLine();
        System.out.print("Enter price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO menu_items (name, category, price) VALUES (?, ?, ?)");
        ps.setString(1, name);
        ps.setString(2, category);
        ps.setDouble(3, price);

        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Menu item added!" : "Failed to add item.");
        ps.close();
    }

    private static void deleteMenuItem(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Menu ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("DELETE FROM menu_items WHERE id=?");
        ps.setInt(1, id);
        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Menu item deleted!" : "Item not found.");
        ps.close();
    }

    private static void viewOrders(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM orders");
        System.out.println("\nOrderID  Date       Payment  Total");
        System.out.println("---------------------------------");
        while (rs.next()) {
            System.out.printf("%-8d %-10s %-7s %.2f%n",
                    rs.getInt("order_id"),
                    rs.getDate("order_date"),
                    rs.getString("payment_method"),
                    rs.getDouble("total"));
        }
        rs.close();
        stmt.close();
    }

    private static void deleteOrder(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Order ID to delete: ");
        int orderId = sc.nextInt();
        sc.nextLine();

        // Удаляем позиции заказа
        PreparedStatement ps1 = conn.prepareStatement("DELETE FROM order_items WHERE order_id=?");
        ps1.setInt(1, orderId);
        ps1.executeUpdate();
        ps1.close();

        // Удаляем сам заказ
        PreparedStatement ps2 = conn.prepareStatement("DELETE FROM orders WHERE order_id=?");
        ps2.setInt(1, orderId);
        int rows = ps2.executeUpdate();
        System.out.println(rows > 0 ? "Order deleted!" : "Order not found.");
        ps2.close();
    }
}