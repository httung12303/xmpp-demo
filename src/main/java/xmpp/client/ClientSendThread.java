package xmpp.client;

import iothread.SendThread;
import socketwrapper.SocketWrapper;

import java.io.IOException;

public class ClientSendThread extends SendThread {
    public ClientSendThread(SocketWrapper wrapper, String message) throws IOException {
        super(wrapper, message);
    }
}
