package network;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import service.LoginService;

public class Server {

    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            String loginId = reader.readLine();
            String password = reader.readLine();

            LoginService loginService = new LoginService();
            boolean isValid = loginService.validateLogin(Integer.parseInt(loginId), password);

            if (isValid) {
                writer.println("SUCCESS");
              
                String command;
                while ((command = reader.readLine()) != null) {
                  
                    if (command.equalsIgnoreCase("LOGOUT")) {
                        break; // Exit the loop to close connection on logout
                    }
                 
                }
            } else {
                writer.println("FAILURE");
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                writer.close();
                socket.close();
                System.out.println("Connection with client closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
