package xmpp.server;

import db.DBManager;
import db.InsertRowThread;
import iothread.ReceiveThread;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import socketwrapper.SocketWrapper;
import stanza.ResultIQ;
import stanza.Stanza;

import javax.xml.xpath.*;
import java.io.IOException;
import java.time.LocalTime;

public class ServerReceiveThread extends ReceiveThread {
    private final DBManager db;

    public ServerReceiveThread(SocketWrapper wrapper, DBManager db) throws IOException {
        super(wrapper);
        this.db = db;
    }

    // Update db
    public void processStanza(Stanza stanza) {
        if (stanza.getType() == Stanza.RESULT_IQ) {
            new InsertRowThread((ResultIQ) stanza, db).run();
        }
    }
}
