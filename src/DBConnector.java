import Shared.Period;
import Shared.Servises;
import Shared.Sale;
import Shared.User;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

class DBConnector
{
    Connection con;

    void connect()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/cpdb";
            String username = "root";
            String password = "root";
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection to DB successful!");
        }
        catch(Exception ex)
        {
            System.out.println("Connection failed...");
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

    ResultSet executeQuery(String query) throws SQLException
    {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
//        while (rs.next()) {
//            System.out.println(rs.getString(1) + "    " + rs.getString(2));// + "    " + rs.getString(3));
//        }
        return rs;
    }

    void executeUpdate(String updateQuery) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate(updateQuery);
    }

    void addActivity(String login) throws SQLException {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String query = "INSERT INTO cpdb.activity values ('" +login+ "', '" + formatter.format(date) + "');";
        System.out.println(date);
        executeUpdate(query);
    }

    User authorize(User user) throws SQLException
    {
        ResultSet rs = executeQuery("select * from cpdb.users\n" +
                "where login = \""+user.getLogin() + "\" and\n" +
                "password = \""+user.getPassword() + "\";");

//        }

        try
        {
            rs.next();
            System.out.println("Авторизация прошла успешно:" + user.getLogin());
            addActivity(user.getLogin());
            return new User(rs.getString(1),
                    rs.getString(2),
                    rs.getString(3));
        }
        catch (Exception ex)
        {
            System.out.println(ex);
//            ex.printStackTrace();
        }
        System.out.println("\n!!!\t\t\tОшибка авторизации");
        return new User(null, null, null);
    }

    User createUser(User user) throws SQLException {
        String query = "insert into cpdb.users values ("+
                "\"" + user.getLogin() + "\", "+
                "\"" + user.getPassword() + "\", "+
                "\"" + user.getType() + "\");";
        executeUpdate(query);
        return user;
    }

    ArrayList<Servises> loadGoods() throws SQLException {
        ArrayList<Servises> list = new ArrayList<Servises>();;
        ResultSet rs = executeQuery("select label, model, price from cpdb.goods");
        while(rs.next())
        {
            list.add(new Servises(rs.getString(1),rs.getString(2),rs.getFloat(3)));
        }
        return list;
    }

    ArrayList<User> loadUsers() throws SQLException {
        ArrayList<User> list = new ArrayList<User>();;
        ResultSet rs = executeQuery("select * from cpdb.users");
        while(rs.next())
        {
            list.add(new User(rs.getString(1),rs.getString(2),rs.getString(3)));
        }
        return list;
    }

    void editUser(User initialData, User finalData) throws SQLException {
        String query = "update cpdb.users "+
                "set login = \"" + finalData.getLogin() +"\", "+
                "password = \"" + finalData.getPassword() + "\", "+
                "type = \"" + finalData.getType() + "\" "+
                "where login = \"" + initialData.getLogin() + "\";";
//        System.out.println(query);
        executeUpdate(query);
    }

    void deleteUser(User user) throws SQLException {
        String query = "delete from cpdb.users "+
                "where login = \"" + user.getLogin() + "\";";
        executeUpdate(query);
    }

    void addUser(User user) throws SQLException {
        String query = "insert into cpdb.users values ("+
                "\"" + user.getLogin() + "\", "+
                "\"" + user.getPassword() + "\", "+
                "\"" + user.getType() + "\");";

        executeUpdate(query);
    }

    ArrayList<Servises> top3Goods(String DESC) throws SQLException {
        ArrayList<Servises> list = new ArrayList<Servises>();
        ResultSet rs = executeQuery(
                "SELECT \n" +
                "goods.label,\n" +
                "goods.model,\n" +
                "(soldgoods.amount * goods.price) AS 'totalprice'\n" +
                "FROM\n" +
                "soldgoods\n" +
                "   INNER JOIN\n" +
                "goods ON goods.id = soldgoods.id\n" +
                "    AND YEAR(cpdb.soldgoods.date) = YEAR(CURRENT_DATE - INTERVAL 1 YEAR)\n" +
                "GROUP BY goods.model\n" +
                "ORDER BY TOTALPRICE " + DESC + "\n" +
                "LIMIT 3;");
        while(rs.next())
        {
            list.add(new Servises(rs.getString(1),rs.getString(2),rs.getFloat(3)));
        }
        return list;
    }

    ArrayList<Period> top3Months(String DESC) throws SQLException {
        ArrayList<Period> list = new ArrayList<Period>();
        ResultSet rs = executeQuery(
                "SELECT \n" +
                "MONTH(soldgoods.date) AS Month,\n" +
                "SUM(soldgoods.amount * goods.price) AS totalprice\n" +
                "FROM\n" +
                "    soldgoods\n" +
                "        INNER JOIN\n" +
                "    goods ON goods.id = soldgoods.id\n" +
                "        AND YEAR(cpdb.soldgoods.date) = YEAR(CURRENT_DATE - INTERVAL 1 YEAR)\n" +
                "GROUP BY Month\n" +
                "ORDER BY totalprice "+ DESC +"\n"+
                "LIMIT 3;");
        while(rs.next())
        {
            list.add(new Period(rs.getString(1),rs.getFloat(2)));
        }
        return list;
    }

    Float fstMonth() throws SQLException {
        Float income = null;
        ResultSet rs = executeQuery(
                "SELECT \n" +
                "    SUM(soldgoods.amount * goods.price) AS income\n" +
                "FROM\n" +
                "    soldgoods\n" +
                "        INNER JOIN\n" +
                "    goods ON soldgoods.id = goods.id\n" +
                "WHERE\n" +
                "    YEAR(soldgoods.date) = YEAR(CURRENT_DATE)\n" +
                "    and MONTH(soldgoods.date) = 1;");
        while(rs.next())
        {
            income = rs.getFloat(1);
        }
        return income;
    }

    Float lastMonth() throws SQLException {
        Float income = null;
        ResultSet rs = executeQuery(
                "SELECT \n" +
                "    SUM(soldgoods.amount * goods.price) AS income\n" +
                "FROM\n" +
                "    soldgoods\n" +
                "        INNER JOIN\n" +
                "    goods ON soldgoods.id = goods.id\n" +
                "WHERE\n" +
                "    YEAR(soldgoods.date) = YEAR(CURRENT_DATE)\n" +
                "    and MONTH(soldgoods.date) = 12;");
        while(rs.next())
        {
            income = rs.getFloat(1);
        }
        return income;
    }

    ArrayList<Period> yearsIncome() throws SQLException {
        ArrayList<Period> list = new ArrayList<Period>();
        ResultSet rs = executeQuery(
                "SELECT\n" +
                "SUM(soldgoods.amount * goods.price) AS income,\n" +
                "YEAR(soldgoods.date) AS YEAR\n" +
                "FROM\n" +
                "   soldgoods\n" +
                "INNER JOIN\n" +
                "    goods ON soldgoods.id = goods.id\n" +
                "GROUP BY YEAR\n" +
                "ORDER BY income;");
        while(rs.next())
        {
            list.add(new Period(rs.getString(2),rs.getFloat(1)));
        }
        return list;
    }

    ArrayList<Period> allYearsIncome() throws SQLException {
        ArrayList<Period> list = new ArrayList<Period>();
        ResultSet rs = executeQuery(
                "SELECT \n" +
                        "    SUM(soldgoods.amount * goods.price) AS income,\n" +
                        "    MONTH(soldgoods.date) AS MONTH, YEAR(soldgoods.date) AS YEAR\n" +
                        "FROM\n" +
                        "    soldgoods\n" +
                        "        INNER JOIN\n" +
                        "    goods ON soldgoods.id = goods.id\n" +
                        "GROUP BY MONTH, YEAR\n" +
                        "ORDER BY YEAR, MONTH;");
        while(rs.next())
        {
            list.add(new Period(rs.getString(2),rs.getFloat(1)));
        }
        return list;
    }

    ArrayList<Servises> allYearsGoods() throws SQLException {
        ArrayList<Servises> list = new ArrayList<Servises>();
        ResultSet rs = executeQuery(
                                "SELECT \n" +
                                "    goods.label,\n" +
                                "    goods.model,\n" +
                                "    SUM(soldgoods.amount) AS amount\n" +
                                "FROM\n" +
                                "    soldgoods\n" +
                                "        INNER JOIN\n" +
                                "    goods ON goods.id = soldgoods.id\n" +
                                "GROUP BY goods.model\n" +
                                "ORDER BY amount DESC;");
        while(rs.next())
        {
//            System.out.println(rs.getString(1) +"\t" + rs.getString(2) +"\t" + rs.getString(3));
            list.add(new Servises(rs.getString(1),rs.getString(2),rs.getFloat(3)));
        }
        return list;
    }

    ArrayList<Sale> loadSales() throws SQLException {
        ArrayList<Sale> list = new ArrayList<Sale>();
        ResultSet rs = executeQuery(
                "SELECT \t\n" +
                        "\tgoods.label,\n" +
                        "\tgoods.model,\n" +
                        "\tsoldgoods.date,\n" +
                        "\tsoldgoods.amount\n" +
                        "FROM                                \n" +
                        "\tsoldgoods \n" +
                        "    INNER JOIN\n" +
                        "    goods on goods.id = soldgoods.id\n" +
                        "    order by soldgoods.date DESC\n" +
                        "    limit 100;");
        while(rs.next())
        {
            list.add(new Sale(rs.getString(1),rs.getString(2),rs.getString(3), rs.getInt(4)));
        }
        return list;
    }

    ArrayList<Servises> loadIDs() throws SQLException {
        ArrayList<Servises> list = new ArrayList<Servises>();
        ResultSet rs = executeQuery(
                "SELECT \n" +
                        "    label, model, id\n" +
                        "FROM\n" +
                        "    cpdb.goods;");
        while(rs.next())
        {
           list.add(new Servises(rs.getString(1),rs.getString(2),rs.getFloat(3)));
        }
        return list;
    }

    void addSale(Sale toAdd) throws SQLException {
        Integer id = null;
        ResultSet rs = executeQuery(
                "SELECT \n" +
                        "id\n" +
                        "FROM\n" +
                        "cpdb.goods\n" +
                        "where goods.label = \"" + toAdd.getLabel() + "\" \n" +
                        "AND goods.model = \"" +  toAdd.getModel() + "\";");
        while(rs.next())
            id = rs.getInt(1);

        String insertQuery =
                "insert into soldgoods values( \"" + id + "\", "+
                        "\"" + toAdd.getAmount()+ "\"," +
                        " \"" + toAdd.getDate()+ "\");";
        executeUpdate(insertQuery);
    }

    void deleteSale(Sale toDelete) throws SQLException {
        Integer id = null;
        ResultSet rs = executeQuery(
                "SELECT \n" +
                        "id\n" +
                        "FROM\n" +
                        "cpdb.goods\n" +
                        "where goods.label = \"" + toDelete.getLabel() + "\" \n" +
                        "AND goods.model = \"" +  toDelete.getModel() + "\";");
        while(rs.next())
            id = rs.getInt(1);

        String deleteQuery =
                "DELETE FROM cpdb.soldgoods \n" +
                        "WHERE\n" +
                        "    date = \"" + toDelete.getDate() +
                        "\" AND id = \"" + id + "\";";
        executeUpdate(deleteQuery);
    }

    void uploadPersonalReport(String username, String tip) throws IOException {
        FileWriter writer = new FileWriter(username + "Report.txt", false);
        writer.write(tip);
        writer.append('\n');
        writer.flush();
    }

    String downloadReport(String username) throws IOException {
            return new String(Files.readAllBytes(Paths.get(username + "Report.txt")));
    }
}