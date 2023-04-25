package xmpp.server;

import Stanza.Stanza;
import iothread.SendThread;
import socketwrapper.SocketWrapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerSendThread extends SendThread {

    public ServerSendThread(SocketWrapper wrapper, Stanza stanza) throws IOException {
        super(wrapper, stanza);
    }
}
