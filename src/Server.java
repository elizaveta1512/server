import Shared.Period;
import Shared.Sale;
import Shared.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server extends Thread {

    private Socket clientSocket;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    Server(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(clientSocket.getInputStream());
        start();
    }

    @Override
    public void run() {
        try {
            DBConnector connector = new DBConnector();
            connector.connect();
            String username = null;
            while (true) {
                String message = (String)ois.readObject();
                System.out.println("Запрос: " + message);
                switch (message) {
                    case "authorization":
                        try {
                            User user = (User)ois.readObject();
                            username = user.getLogin();
                            oos.writeObject(connector.authorize(user));
                            oos.flush();
                        } catch (SQLException e) {
                            oos.writeObject(new User(null, null, null));
                            oos.flush();
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "createUser":
                        try {
                            Object user = ois.readObject();
                            oos.writeObject(connector.createUser((User)user));
                            oos.flush();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            oos.writeObject(new User(null,null,null));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "loadGoods":
                        try {
                            oos.writeObject(connector.loadGoods());
                            oos.flush();
                        } catch (SQLException e) {
                            oos.writeObject(new ArrayList<Period>());
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "loadUsers":
                        try {
                            oos.writeObject(connector.loadUsers());
                            oos.flush();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "editUser":
                        try {
                            connector.editUser((User)ois.readObject(), (User)ois.readObject());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "deleteUser":
                        try {
                            connector.deleteUser((User)ois.readObject());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "addUser":
                        try {
                            connector.addUser((User)ois.readObject());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "topGoods":
                        try{
                            oos.writeObject(connector.top3Goods("DESC"));
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "bottomGoods":
                        try{
                            oos.writeObject(connector.top3Goods(""));
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "topMonths":
                        try{
                            oos.writeObject(connector.top3Months("DESC"));
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "bottomMonths":
                        try{
                            oos.writeObject(connector.top3Months(""));
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "fstMonth":
                        try{
                            oos.writeObject(connector.fstMonth());
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "lastMonth":
                        try{
                            oos.writeObject(connector.lastMonth());
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "yearsIncome":
                        try{
                            oos.writeObject(connector.yearsIncome());
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "allYearsIncome":
                        try{
                            oos.writeObject(connector.allYearsIncome());
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "allYearsGoods":
                        try{
                            oos.writeObject(connector.allYearsGoods());
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "loadSales":
                        try{
                            oos.writeObject(connector.loadIDs());
                            oos.flush();
                            oos.writeObject(connector.loadSales());
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "addSale":
                        try{
                            connector.addSale((Sale)ois.readObject());
                            oos.flush();
                            oos.writeObject("ok");
                            oos.flush();
                        }
                        catch (SQLException e) {
                            oos.writeObject(null);
                            oos.flush();
                            e.printStackTrace();
                        }
                        break;
                    case "deleteSale":
                        try{
                            connector.deleteSale((Sale)ois.readObject());
                            oos.flush();
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "uploadPersonalReport":
                        try {
                            connector.uploadPersonalReport(username, (String) ois.readObject());
                            oos.writeObject("ok");
                            oos.flush();
                        } catch (IOException e) {
                            oos.writeObject(null);
//                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "downloadReport":
                        try {
                            oos.writeObject(connector.downloadReport(username));
                            oos.flush();
                        } catch (IOException e) {
                            oos.writeObject(null);
                            oos.flush();
//                            e.printStackTrace();
                        }
                        break;
                    case "exit":
                        System.out.println("Пользователь вышел.");
                        clientSocket.close();
                        return;
                }
            }
        } catch (SocketException e) {
            System.out.println("Ошибка на другом конце :/");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
