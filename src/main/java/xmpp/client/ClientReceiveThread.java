package xmpp.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientReceiveThread extends Thread {
    private Socket clientSocket;
    private DataInputStream input;

    public ClientReceiveThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.input = new DataInputStream(clientSocket.getInputStream());
    }

    // We'll change this to return Stanza later on.
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

    public void closeConn() throws IOException {
        if(clientSocket != null) {
            clientSocket.close();
        }
        if(input != null) {
            input.close();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                boolean connected = clientSocket.isConnected() && !clientSocket.isClosed();
                if (!connected) {
                    break;
                }
                receiveMessage();
                // Do something
            } catch (IOException e) {
                try {
                    closeConn();
                } catch(IOException ex) {

                }
            }
        }
    }
}
