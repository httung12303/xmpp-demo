package xmpp.server;

import iothread.SendThread;
import socketwrapper.SocketWrapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerSendThread extends SendThread {

    public ServerSendThread(SocketWrapper wrapper, String message) throws IOException {
        super(wrapper, message);
    }
}
