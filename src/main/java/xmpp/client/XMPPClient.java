package xmpp.client;

import socketwrapper.SocketWrapper;
import stanza.Stanza;

import java.io.IOException;
import java.net.*;

public class XMPPClient {
    private SocketWrapper clientSocket;
    private Thread receiveThread;

    public XMPPClient(String ip, int port) throws IOException {
        clientSocket = new SocketWrapper(new Socket(ip, port));
    }

    public void startReceive() throws IOException {
        receiveThread = new ClientReceiveThread(getClientSocket());
        receiveThread.start();
    }

    public void sendStanza(Stanza stanza) throws IOException {
        Thread thread = new ClientSendThread(clientSocket, stanza);
        thread.start();
    }

    public static void sendExample() {

    }

    public static void receiveExample() {
        try {
            XMPPClient client = new XMPPClient("127.0.0.1", 10000);
            client.startReceive();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        XMPPClient.sendExample();
    }

    public SocketWrapper getClientSocket() {
        return clientSocket;
    }
}
