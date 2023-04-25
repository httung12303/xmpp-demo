package xmpp.server;

import stanza.Stanza;
import iothread.SendThread;
import socketwrapper.SocketWrapper;

import java.io.IOException;

public class ServerSendThread extends SendThread {

    public ServerSendThread(SocketWrapper wrapper, Stanza stanza) throws IOException {
        super(wrapper, stanza);
    }
}
