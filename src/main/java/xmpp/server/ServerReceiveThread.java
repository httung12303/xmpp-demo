package xmpp.server;

import iothread.ReceiveThread;
import socketwrapper.SocketWrapper;
import stanza.Stanza;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerReceiveThread extends ReceiveThread {

    public ServerReceiveThread(SocketWrapper wrapper) throws IOException {
        super(wrapper);
    }

    // Update db
    public void processStanza(Stanza stanza) {

    }
}
