package xmpp.client;

import socketwrapper.SocketWrapper;

import java.io.IOException;
import java.net.*;

public class XMPPClient {
    private SocketWrapper clientSocket;
    private Thread receiveThread;

    public XMPPClient(String ip, int port) throws IOException {
        clientSocket = new SocketWrapper(new Socket(ip, port));
    }

    public void startReceiving() throws IOException {
        receiveThread = new ClientReceiveThread(getClientSocket());
        receiveThread.start();
    }

    public void sendMessage(String message) throws IOException {
        Thread thread = new ClientSendThread(clientSocket, message);
        thread.start();
    }

    public static void sendExample() {
        try {
            XMPPClient client = new XMPPClient("127.0.0.1", 10000);
            client.sendMessage("abcascsadsddasdasdasdsdad");
            client.sendMessage("abcascsadsddasdasdasdsdad1");
            client.sendMessage("abcascsadsddasdasdasdsdad2");
            client.sendMessage("abcascsadsddasdasdasdsdad3");
            client.sendMessage("abcascsadsddasdasdasdsdad5");
            client.sendMessage("abcascsadsddasdasdasdsdad6");
            client.sendMessage("abcascsadsddasdasdasdsdad7");
            client.sendMessage("abcascsadsddasdasdasdsdad8");
            client.sendMessage("abcascsadsddasdasdasdsdad9");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void receiveExample() {
        try {
            XMPPClient client = new XMPPClient("127.0.0.1", 10000);
            client.startReceiving();
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
