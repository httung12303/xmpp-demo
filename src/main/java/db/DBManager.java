package db;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

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

    public void insertIntoClientsTable(String jid, String time, String temperature, String humidity, String brightness, String delay) throws SQLException, ParseException {
        String query = "INSERT INTO clients(jid, time, temperature, humidity, brightness, delay, last_update) VALUES (?, ?, ?, ?, ?, ?, ?)"
                + "ON DUPLICATE KEY UPDATE "
                + "time = VALUES(time), "
                + "temperature = VALUES(temperature), "
                + "humidity = VALUES(humidity), "
                + "brightness = VALUES(brightness), "
                + "delay = VALUES(delay), "
                + "last_update = VALUES(last_update)"
                + ";";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, jid);
        statement.setTime(2, new Time(new SimpleDateFormat("HH:mm:ss").parse(time).getTime()));
        statement.setFloat(3, Float.parseFloat(temperature));
        statement.setInt(4, Integer.parseInt(humidity));
        statement.setInt(5, Integer.parseInt(brightness));
        statement.setLong(6, Long.parseLong(delay));
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        statement.setLong(7, now);
        statement.executeUpdate();
        statement.close();
    }

    public ResultSet executeSelectQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }
}
