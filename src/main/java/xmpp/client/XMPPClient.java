package xmpp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class XMPPClient {
    private Socket clientSocket;
    private Thread rcvThread;
    private Thread sndThread;
    private DataInputStream input;
    private DataOutputStream output;

    public XMPPClient(String ip, int port) {

        try {
            clientSocket = new Socket(ip,port);
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
//            rcvThread = new ClientReceiveThread(clientSocket, input, output);
//            sndThread = new ClientSendThread(clientSocket, input, output);
//            rcvThread.start();
//            sndThread.start();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        XMPPClient client = new XMPPClient("127.0.0.1", 10000);
    }
}
