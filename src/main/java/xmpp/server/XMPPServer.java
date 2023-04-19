package xmpp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class XMPPServer {
    private final ServerSocket serverSocket;
    public XMPPServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    public Socket acceptConn() throws IOException {
        return serverSocket.accept();
    }
    public void sendMessage(ConnSocketWrapper wrapper, String message) throws IOException {
        Thread thread = new ServerSendThread(wrapper, message);
        thread.start();
    }
    public static void sendExample() {
        try {
            XMPPServer server = new XMPPServer(10000);
            while (true) {
                Socket connSocket = server.acceptConn();
                ConnSocketWrapper connSocketWrapper = new ConnSocketWrapper(connSocket);
                server.sendMessage(connSocketWrapper, "Hello");
                server.sendMessage(connSocketWrapper, "HI");
                server.sendMessage(connSocketWrapper, "Xin chao");
                server.sendMessage(connSocketWrapper, "Bonjour");
                server.sendMessage(connSocketWrapper, "Si");
                server.sendMessage(connSocketWrapper, "Escobar");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        XMPPServer.sendExample();
    }
}
