package xmpp.client;

import socketwrapper.SocketWrapper;
import stanza.QueryIQ;
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
        JID = String.format("%d@%s", socket.getLocalPort(), socket.getLocalAddress().toString().replace("/", ""));
        serverJID = String.format("%d@%s", socket.getPort(), socket.getInetAddress().toString().replace("/", ""));
        environment = new Environment();
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

    public void startInfoSendTimer() {
        TimerTask intervalSend = new TimerTask() {
            @Override
            public void run() {
                try {
                    sendStanza(getState());
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

    public void startQuerySendTimer() {
        TimerTask intervalSend = new TimerTask() {
            @Override
            public void run() {
                try {
                    sendStanza(createQueryIQ());
                } catch (Exception e) {

                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(intervalSend, 1000, 10000);
    }
    public Stanza createQueryIQ() throws ParserConfigurationException {
        QueryIQ queryIQ = new QueryIQ(JID, serverJID);
        queryIQ.addQuery("temperature");
        queryIQ.addQuery("humidity");
        queryIQ.addQuery("brightness");
        return queryIQ;
    }

    public static void main(String[] args) {
        try {
            XMPPClient client = new XMPPClient("127.0.0.1", 10000);
            client.startReceiveThread();
            client.startInfoSendTimer();
            client.startQuerySendTimer();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
