import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Database db = new Database();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line;
            List<OrderItem> items = new ArrayList<>();
            int orderId = 0;
            String date = "";
            String payment = "";

            out.println("Connected to Smart Cafe Server!");

            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("MENU")) {
                    // Отправляем клиенту все блюда
                    try (Connection conn = DriverManager.getConnection(Database.URL, Database.USER, Database.PASSWORD);
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items")) {

                        out.println("--- MENU ---");
                        out.println(String.format("%-3s %-16s %-10s %-5s", "ID", "Name", "Category", "Price"));
                        while (rs.next()) {
                            out.println(String.format("%-3d %-16s %-10s %.2f",
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("category"),
                                    rs.getDouble("price")));
                        }
                        out.println("------------");

                    } catch (SQLException e) {
                        out.println("Error fetching menu: " + e.getMessage());
                    }
                } else if (line.startsWith("ORDER")) {
                    String[] parts = line.split(";");
                    orderId = Integer.parseInt(parts[1]);
                    date = parts[2];
                    payment = parts[3];
                } else if (line.startsWith("ITEM")) {
                    String[] parts = line.split(";");
                    int menuId = Integer.parseInt(parts[1]);
                    int qty = Integer.parseInt(parts[2]);
                    items.add(new OrderItem(menuId, qty));
                } else if (line.equalsIgnoreCase("END")) {
                    double total = db.saveOrder(orderId, date, payment, items);
                    out.println("Order saved! Total = " + total);
                    items.clear();
                } else {
                    out.println("Unknown command");
                }
            }

        } catch (IOException | SQLException e) {
            System.err.println("Client disconnected or error: " + e.getMessage());
        }
    }
}
