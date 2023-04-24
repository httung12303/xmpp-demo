package db;
import java.sql.*;
public class DBManager {
    private final String USER = "root";
    private final String PASSWORD = "123456789";
    private final Connection connection;
    public DBManager() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys", USER, PASSWORD);
        System.out.println(connection);
    }
    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }
    public static void main(String[] args) {
        try {
            DBManager dbManager = new DBManager();
            ResultSet resultSet = dbManager.executeQuery("SELECT * FROM host_summary LIMIT 1;");
            while(resultSet.next()) {
                System.out.println(resultSet.getString(1));
                System.out.println(resultSet.getString(2));
                System.out.println(resultSet.getString(3));
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: "+ e.getMessage());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
