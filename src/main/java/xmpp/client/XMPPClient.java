package xmpp.client;

import socketwrapper.SocketWrapper;

import java.io.IOException;
import java.net.*;

public class XMPPClient {
    private SocketWrapper clientSocket;
    private Thread receiveThread;

    public XMPPClient(String ip, int port) throws IOException {
        clientSocket = new SocketWrapper(new Socket(ip, port));
        receiveThread = new ClientReceiveThread(clientSocket);
        receiveThread.start();
    }

    public static void main(String[] args) {
        try {
            XMPPClient client = new XMPPClient("127.0.0.1", 10000);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
