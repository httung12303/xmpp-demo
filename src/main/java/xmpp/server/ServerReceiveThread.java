package xmpp.server;

import db.DBManager;
import db.InsertRowThread;
import iothread.ReceiveThread;
import socketwrapper.SocketWrapper;
import stanza.QueryIQ;
import stanza.ResultIQ;
import stanza.Stanza;

import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class SendQueryResponseThread extends Thread {
    private SocketWrapper socketWrapper;
    private QueryIQ iq;
    private DBManager db;

    public SendQueryResponseThread(SocketWrapper socketWrapper, QueryIQ iq, DBManager db) {
        this.db = db;
        this.socketWrapper = socketWrapper;
        this.iq = iq;
    }
    @Override
    public void run() {
        try {
            String clientTime = QueryIQ.getTime(iq);
            String query = String.format("SELECT * FROM recommendation WHERE HOUR(time)=HOUR('%s');", clientTime);
            ResultSet rs = db.executeSelectQuery(query);
            if(rs.next()) {
                String temperature = rs.getString("temperature");
                String humidity = rs.getString("humidity");
                String brightness = rs.getString("brightness");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String formattedTime = LocalTime.now().format(formatter);
                ResultIQ res = new ResultIQ(Stanza.getSReceiver(iq), Stanza.getSender(iq), formattedTime);
                res.addItem("temperature", temperature);
                res.addItem("humidity", humidity);
                res.addItem("brightness", brightness);
                new ServerSendThread(socketWrapper, res).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

public class ServerReceiveThread extends ReceiveThread {
    private final DBManager db;

    public ServerReceiveThread(SocketWrapper wrapper, DBManager db) throws IOException {
        super(wrapper);
        this.db = db;
    }

    // Update db
    public void processStanza(Stanza stanza) {
        if (stanza.getType() == Stanza.RESULT_IQ) {
            new InsertRowThread((ResultIQ) stanza, db).start();
        } else if (stanza.getType() == Stanza.QUERY_IQ) {
            new SendQueryResponseThread(this.getSocketWrapper(), (QueryIQ) stanza, db).start();
        }
    }
}
