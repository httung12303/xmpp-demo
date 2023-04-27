package db;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;

public class DBManager {
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/xmpp_demo";
    private final String USER = "root";
    private final String PASSWORD = "123456789";
    private final Connection connection;

    public DBManager() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        startClientRemoveThread();
    }

    public void startClientRemoveThread() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    removeOfflineClients();
                } catch (Exception e) {

                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000, 10000);
    }

    public void insertIntoClientsTable(String jid, String time, float temperature, int humidity, int brightness, long delay, float goodput) throws SQLException, ParseException {
        String query = "INSERT INTO clients(jid, time, temperature, humidity, brightness, delay, goodput, last_update) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                + "ON DUPLICATE KEY UPDATE "
                + "time = VALUES(time), "
                + "temperature = VALUES(temperature), "
                + "humidity = VALUES(humidity), "
                + "brightness = VALUES(brightness), "
                + "delay = VALUES(delay), "
                + "last_update = VALUES(last_update), "
                + "goodput = VALUES(goodput)"
                + ";";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, jid);
        statement.setTime(2, new Time(new SimpleDateFormat("HH:mm:ss").parse(time).getTime()));
        statement.setFloat(3, temperature);
        statement.setInt(4, humidity);
        statement.setInt(5, brightness);
        statement.setLong(6, delay);
        statement.setFloat(7, goodput);
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        statement.setLong(8, now);
        statement.executeUpdate();
        statement.close();
    }

    public ResultSet executeSelectQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    private void removeOfflineClients() throws SQLException {
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        ResultSet clients = executeSelectQuery("SELECT jid, last_update FROM clients;");
        while (clients.next()) {
            String jid = clients.getString("jid");
            long lastUpdate = Long.parseLong(clients.getString("last_update"));
            if (now - lastUpdate > 10000) {
                deleteRow(jid);
            }
        }
    }

    private void deleteRow(String jid) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.format("DELETE FROM clients WHERE jid = '%s'", jid));
    }
}
