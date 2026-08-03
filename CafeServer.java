import java.io.*;
import java.net.*;

public class CafeServer
{
    public static void main(String[] args)
    {
        final int PORT = 8080;
        System.out.println("=== Smart Cafe Server Started ===");

        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Server running on port " + PORT);

            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e)
            {
                e.printStackTrace();
            }
    }
}


