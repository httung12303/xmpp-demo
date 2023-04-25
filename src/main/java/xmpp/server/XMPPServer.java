package xmpp.server;

import stanza.Stanza;
import socketwrapper.SocketWrapper;

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
    public void sendStanza(SocketWrapper wrapper, Stanza stanza) throws IOException {
        Thread thread = new ServerSendThread(wrapper, stanza);
        thread.start();
    }
    public static void sendExample() {
        // The real implementation is similar except the loop does not do the sending
        // but starts a receiving thread immediately.
        // Since each connection listen on a separate DataInputStream, synchronization is not a problem, not foe the
        // receiving side at least.
        try {
            XMPPServer server = new XMPPServer(10000);
            while (true) {
                Socket connSocket = server.acceptConn();
                SocketWrapper socketWrapper = new SocketWrapper(connSocket);

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void receiveExample() {
        try {
            XMPPServer server = new XMPPServer(10000);
            while(true) {
                Socket connSocket = server.acceptConn();
                SocketWrapper socketWrapper = new SocketWrapper(connSocket);
                new ServerReceiveThread(socketWrapper).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        XMPPServer.receiveExample();
    }
}
