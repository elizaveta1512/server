import Shared.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class main
{
    private static Socket clientSocket;
    private static ServerSocket server;

    public static void main(String[] args) throws IOException {
        try {
            server = new ServerSocket(8080);
            System.out.println("СТАРТУЕМ!");

            DBConnector connector = new DBConnector();
            connector.connect();

            try {
                connector.authorize(new User("admins", "admin", "admin"));
            } catch (SQLException e) {
                e.printStackTrace();
            }

///////////////////////////////
            while (true) {
                clientSocket = server.accept();
                System.out.println("new connection" + clientSocket.getInetAddress());
                new Server(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        finally {
//            server.close();
//            clientSocket.close();
//        }
    }
}
