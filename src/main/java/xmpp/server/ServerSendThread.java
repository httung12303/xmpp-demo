package xmpp.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerSendThread extends Thread {
    private final Socket connSocket;
    private final DataOutputStream output;
    private final String message;
    public ServerSendThread(ConnSocketWrapper wrapper, String message) throws IOException {
        this.connSocket = wrapper.getConnSocket();
        this.output = wrapper.getOutputStream();
        this.message = message;
    }
    // Synchronization is needed because we might write multiple messages to client at the same time.
    public void sendMessage(String message) throws IOException {
        synchronized (output) {
            byte[] b = message.getBytes();
            output.writeInt(b.length);
            output.write(b);
            output.flush();
        }
    }
    @Override
    public void run() {
        try {
            boolean connected = connSocket.isConnected() && !connSocket.isClosed();
            if(!connected) {
                return;
            }
            sendMessage(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
