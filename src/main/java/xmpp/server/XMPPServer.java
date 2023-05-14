package xmpp.server;

import db.DBManager;
import stanza.ResultIQ;
import stanza.Stanza;
import socketwrapper.SocketWrapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class XMPPServer {
    private final ServerSocket serverSocket;
    private final DBManager db;

    public XMPPServer(int port) throws IOException, SQLException, ClassNotFoundException {
        serverSocket = new ServerSocket(port);
        db = new DBManager();
    }

    public void startReceiveThread(SocketWrapper socketWrapper) throws IOException {
        new ServerReceiveThread(socketWrapper, this.db).start();
    }

    public void start() {
        try {
            while (true) {
                Socket connSocket = serverSocket.accept();
                SocketWrapper socketWrapper = new SocketWrapper(connSocket);
                startReceiveThread(socketWrapper);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            XMPPServer server = new XMPPServer(10000);
            server.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
