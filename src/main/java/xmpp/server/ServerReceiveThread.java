package xmpp.server;

import socketwrapper.SocketWrapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerReceiveThread extends Thread {
    private final Socket connSocket;
    private final DataInputStream input;
    public ServerReceiveThread(SocketWrapper wrapper) {
        this.connSocket = wrapper.getConnSocket();
        this.input = wrapper.getInputStream();
    }
    public String receiveMessage() throws IOException {
        String result = "";
        int remainingLen = input.readInt();
        int bytes;
        byte[] buffer = new byte[4 * 1024];
        while (remainingLen > 0 && (bytes = input.read(buffer, 0, (int) Math.min(remainingLen, buffer.length))) != -1) {
            remainingLen -= bytes;
            String temp = new String(buffer, 0, bytes, StandardCharsets.UTF_8);
            result += temp;
        }
        System.out.println(result);
        return result;
    }
    public void processMessage(String message) {
        // Do something w db maybe ???
    }
    @Override
    public void run() {
        while(true) {
            try {
                String message = receiveMessage();
                processMessage(message);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
