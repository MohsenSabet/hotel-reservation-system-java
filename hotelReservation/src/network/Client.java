 package network;

import java.io.*;
import java.net.*;

public class Client {

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public Client(String address, int port) throws IOException {
        socket = new Socket(address, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendCredentials(String loginId, String password) {
        writer.println(loginId);
        writer.println(password);
    }

    public String getResponse() throws IOException {
        return reader.readLine();
    }

    public void sendCommand(String command) {
        writer.println(command);
    }

    public void logout() throws IOException {
        sendCommand("LOGOUT");
        close();
    }

    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}