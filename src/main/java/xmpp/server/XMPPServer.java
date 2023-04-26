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

    public Socket acceptConn() throws IOException {
        return serverSocket.accept();
    }

    public void sendStanza(SocketWrapper wrapper, Stanza stanza) throws IOException {
        Thread thread = new ServerSendThread(wrapper, stanza);
        thread.start();
    }

    public void startReceiveThread(SocketWrapper socketWrapper) throws IOException {
        new ServerReceiveThread(socketWrapper, this.db).start();
    }

    public static void receiveExample() {
        try {
            XMPPServer server = new XMPPServer(10000);
            while (true) {
                Socket connSocket = server.acceptConn();
                SocketWrapper socketWrapper = new SocketWrapper(connSocket);
                server.startReceiveThread(socketWrapper);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        XMPPServer.receiveExample();
    }
}
