package xmpp.client;

import iothread.SendThread;
import socketwrapper.SocketWrapper;
import stanza.Stanza;

import java.io.IOException;

public class ClientSendThread extends SendThread {
    public ClientSendThread(SocketWrapper wrapper, Stanza stanza) throws IOException {
        super(wrapper, stanza);
    }
}
