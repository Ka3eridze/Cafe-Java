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
            System.out.println(in.readLine()); // Connected message

            boolean running = true;
            while (running) {
                System.out.println("\n=== Smart Cafe ===");
                System.out.println("1. Сделать заказ");
                System.out.println("0. Выйти");
                System.out.print("Выбор: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        // Запрашиваем меню у сервера
                        out.println("MENU");
                        String menuLine;
                        while (!(menuLine = in.readLine()).equals("------------")) {
                            System.out.println(menuLine);
                        }
                        System.out.println(menuLine); // печатаем "------------"

                        // Ввод заказа
                        System.out.print("Введите Order ID: ");
                        int orderId = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Введите дату (YYYY-MM-DD): ");
                        String date = sc.nextLine();

                        System.out.print("Способ оплаты (Cash/Card): ");
                        String payment = sc.nextLine();

                        out.println("ORDER;" + orderId + ";" + date + ";" + payment);

                        boolean adding = true;
                        while (adding) {
                            System.out.print("Введите ID блюда: ");
                            int menuId = sc.nextInt();
                            System.out.print("Введите количество: ");
                            int qty = sc.nextInt();
                            sc.nextLine();

                            out.println("ITEM;" + menuId + ";" + qty);

                            System.out.print("Добавить ещё? (y/n): ");
                            String more = sc.nextLine();
                            if (more.equalsIgnoreCase("n")) adding = false;
                        }

                        out.println("END");
                        System.out.println(in.readLine()); // total
                    }
                    case 0 -> {
                        running = false;
                        System.out.println("Выход...");
                    }
                    default -> System.out.println("Неверный выбор!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
