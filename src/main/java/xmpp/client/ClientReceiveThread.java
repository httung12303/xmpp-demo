package xmpp.client;

import iothread.ReceiveThread;
import socketwrapper.SocketWrapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientReceiveThread extends ReceiveThread {

    public ClientReceiveThread(SocketWrapper wrapper) throws IOException {
        super(wrapper);
    }
}
