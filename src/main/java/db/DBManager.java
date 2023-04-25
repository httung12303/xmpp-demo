package db;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class DBManager {
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/xmpp_demo";
    private final String USER = "root";
    private final String PASSWORD = "123456789";
    private final Connection connection;

    public DBManager() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        System.out.println(connection);
    }

    public void insertIntoClientsTable(String jid, String time, String temperature, String humidity, String brightness, String last_update) throws SQLException, ParseException {
        String query = "INSERT INTO clients(jid, time, temperature, humidity, brightness, last_update) VALUES (?, ?, ?, ?, ?, ?)"
                + "ON DUPLICATE KEY UPDATE "
                + "time = VALUES(time), "
                + "temperature = VALUES(temperature), "
                + "humidity = VALUES(humidity), "
                + "brightness = VALUES(brightness), "
                + "last_update = VALUES(last_update)"
                + ";";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, jid);
        statement.setTime(2, new Time(new SimpleDateFormat("HH:mm:ss").parse(time).getTime()));
        statement.setFloat(3, Float.valueOf(temperature));
        statement.setInt(4, Integer.valueOf(humidity));
        statement.setInt(5, Integer.valueOf(brightness));
        statement.setTime(6, new Time(new SimpleDateFormat("HH:mm:ss").parse(last_update).getTime()));
        statement.executeUpdate();
        statement.close();
    }

    public ResultSet executeSelectQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }
}
