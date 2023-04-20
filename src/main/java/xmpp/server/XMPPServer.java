package xmpp.server;

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
    public void sendMessage(SocketWrapper wrapper, String message) throws IOException {
        Thread thread = new ServerSendThread(wrapper, message);
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
                // Start a receive thread, which listens to messages and interact with db based on the messages' content.
                server.sendMessage(socketWrapper, "Hello alibabababababababababađâsd alibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsd");
                server.sendMessage(socketWrapper, "HI alibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsd");
                server.sendMessage(socketWrapper, "Xin chao alibabababababababababađâsd");
                server.sendMessage(socketWrapper, "Bonjour alibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsdalibabababababababababađâsd");
                server.sendMessage(socketWrapper, "Si alibabababababababababađâsd");
                server.sendMessage(socketWrapper, "Escobar alibabababababababababađâsd");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        XMPPServer.sendExample();
    }
}
