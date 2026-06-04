import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CafeClient {
    public static void main(String[] args) {
        final String SERVER = "localhost";
        final int PORT = 8080;

        try (
                Socket socket = new Socket(SERVER, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner sc = new Scanner(System.in)
        ) {
            System.out.println(in.readLine()); //connected message

            boolean running = true;
            while (running) {
                System.out.println("\n=== Smart Cafe ===");
                System.out.println("1. Make an order");
                System.out.println("0. Exit");
                System.out.print("Choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        //ask menu from server
                        out.println("MENU");
                        String menuLine;
                        while (!(menuLine = in.readLine()).equals("------------")) {
                            System.out.println(menuLine);
                        }
                        System.out.println(menuLine);

                        //order output
                        System.out.print("Enter Order ID: ");
                        int orderId = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter the date (YYYY-MM-DD): ");
                        String date = sc.nextLine();

                        System.out.print("Payment method (Cash/Card): ");
                        String payment = sc.nextLine();

                        out.println("ORDER;" + orderId + ";" + date + ";" + payment);

                        boolean adding = true;
                        while (adding) {
                            System.out.print("Enter meal ID: ");
                            int menuId = sc.nextInt();
                            System.out.print("Quantity: ");
                            int qty = sc.nextInt();
                            sc.nextLine();

                            out.println("ITEM;" + menuId + ";" + qty);

                            System.out.print("Add more? (y/n): ");
                            String more = sc.nextLine();
                            if (more.equalsIgnoreCase("n")) adding = false;
                        }

                        out.println("END");
                        System.out.println(in.readLine()); //total
                    }
                    case 0 -> {
                        running = false;
                        System.out.println("Exiting...");
                    }
                    default -> System.out.println("Wrong choce!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
