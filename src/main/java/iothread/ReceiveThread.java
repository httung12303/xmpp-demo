package iothread;

import socketwrapper.SocketWrapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public abstract class ReceiveThread extends Thread {
    private final SocketWrapper socketWrapper;

    public ReceiveThread(SocketWrapper wrapper) throws IOException {
        this.socketWrapper = wrapper;
    }

    // We'll change this to return Stanza later on.
    public String receiveMessage() throws IOException {
        String result = "";
        final DataInputStream input = socketWrapper.getInputStream();
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

    @Override
    public void run() {
        while (true) {
            try {
                if (!socketWrapper.connected()) {
                    break;
                }
                receiveMessage();
                // Do something
            } catch (IOException e) {
                System.out.println(e.getMessage());
                socketWrapper.close();
            }
        }
    }
}
