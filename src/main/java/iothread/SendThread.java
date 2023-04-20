package iothread;

import socketwrapper.SocketWrapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class SendThread extends Thread {
    private final SocketWrapper socketWrapper;
    private final String message;
    public SendThread(SocketWrapper wrapper, String message) throws IOException {
        this.socketWrapper = wrapper;
        this.message = message;
    }
    // Synchronization is needed because we might write multiple messages to client at the same time.
    public void sendMessage(String message) throws IOException {
        final DataOutputStream output = socketWrapper.getOutputStream();
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
            if(!socketWrapper.connected()) {
                return;
            }
            sendMessage(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            socketWrapper.close();
        }
    }
}
