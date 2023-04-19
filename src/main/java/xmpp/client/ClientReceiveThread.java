package xmpp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientReceiveThread extends Thread {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientReceiveThread(Socket clientSocket, DataInputStream input, DataOutputStream output) {
        this.clientSocket = clientSocket;
        this.input = input;
        this.output = output;
    }

    // We'll change this to return Stanza later on.
    public String receiveMessage() throws IOException {
        String result = "";
        long remainingLen = input.readLong();
        int bytes;
        byte[] buffer = new byte[4 * 1024];
        // Remember to synchronize when writing to this socket via its input stream.(later in server)
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
        if (output != null) {
            output.close();
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
                // We close conn here for testing.
                closeConn();
            } catch (IOException e) {
                try {
                    closeConn();
                } catch(IOException ex) {

                }
            }
        }
    }

    public static void main(String[] args) {
        byte[] b = {65, 97, 95, 95};
        System.out.println(new String(b, StandardCharsets.UTF_8));
    }
}
