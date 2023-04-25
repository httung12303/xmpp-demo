package xmpp.client;

import socketwrapper.SocketWrapper;
import stanza.ResultIQ;
import stanza.Stanza;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class XMPPClient {
    private SocketWrapper clientSocket;
    private Thread receiveThread;
    private String JID;
    private String serverJID;
    private Environment environment;

    public XMPPClient(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        clientSocket = new SocketWrapper(socket);
        JID = String.format("%d@%s", socket.getLocalPort(), socket.getLocalAddress());
        serverJID = String.format("%d@%s", socket.getPort(), socket.getInetAddress());
        environment = new Environment();
    }

    public boolean connected() {
        return this.clientSocket.connected();
    }

    public void startReceiveThread() throws IOException {
        receiveThread = new ClientReceiveThread(clientSocket);
        receiveThread.start();
    }

    public Stanza getState() throws ParserConfigurationException {
        ResultIQ state = new ResultIQ(JID, serverJID);
        state.addItem("time", environment.getTime().toString());
        state.addItem("temperature", Float.toString(environment.getTemperature()));
        state.addItem("humidity", Integer.toString(environment.getHumidity()));
        state.addItem("brightness", Integer.toString(environment.getBrightness()));
        return state;
    }

    public void startInfoSend() {
        TimerTask intervalSend = new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread sendThread = new ClientSendThread(clientSocket, getState());
                    sendThread.start();
                } catch (Exception e) {

                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(intervalSend, 1000, 2000);
    }

    public void sendStanza(Stanza stanza) throws IOException {
        Thread thread = new ClientSendThread(clientSocket, stanza);
        thread.start();
    }

    public static void main(String[] args) {
        try {
            XMPPClient client = new XMPPClient("127.0.0.1", 10000);
            client.startReceiveThread();
            client.startInfoSend();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
