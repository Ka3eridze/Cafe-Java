import java.sql.*;
import java.util.List;

public class Database {
    public static final String URL = "jdbc:mysql://localhost:3306/cafe_db?useSSL=false&serverTimezone=UTC";
    public static final String USER = "root";
    public static final String PASSWORD = "Heavyd1rtyso/ul"; // поменяй на свой

    public double saveOrder(int orderId, String date, String payment, List<OrderItem> items) throws SQLException {
        double total = 0;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            for (OrderItem item : items) {
                PreparedStatement ps = conn.prepareStatement("SELECT price FROM menu_items WHERE id=?");
                ps.setInt(1, item.getMenuId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    total += rs.getDouble("price") * item.getQuantity();
                }
                rs.close();
                ps.close();
            }

            PreparedStatement psOrder = conn.prepareStatement(
                    "INSERT INTO orders (order_id, order_date, payment_method, total) VALUES (?, ?, ?, ?)"
            );
            psOrder.setInt(1, orderId);
            psOrder.setDate(2, Date.valueOf(date));
            psOrder.setString(3, payment);
            psOrder.setDouble(4, total);
            psOrder.executeUpdate();
            psOrder.close();

            for (OrderItem item : items) {
                PreparedStatement psItem = conn.prepareStatement(
                        "INSERT INTO order_items (order_id, menu_id, quantity) VALUES (?, ?, ?)"
                );
                psItem.setInt(1, orderId);
                psItem.setInt(2, item.getMenuId());
                psItem.setInt(3, item.getQuantity());
                psItem.executeUpdate();
                psItem.close();
            }

            conn.commit();
        }
        return total;
    }
}