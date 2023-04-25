package xmpp.server;

import db.DBManager;
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
            processResultIQ((ResultIQ) stanza);
        }
    }

    private void processResultIQ(ResultIQ iq) {
        try {
            String jid = getJID(iq);
            String time = getInfo(iq, "time");
            String temperature = getInfo(iq, "temperature");
            String humidity = getInfo(iq, "humidity");
            String brightness = getInfo(iq, "brightness");
            String last_update = String.valueOf(LocalTime.now());
            db.insertIntoClientsTable(jid, time, temperature, humidity, brightness, last_update);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private String getInfo(ResultIQ iq, String info) throws XPathExpressionException {
        Document doc = iq.getDocument();
        String expression = String.format("//item[@info='%s']", info);
        XPath xPath = XPathFactory.newInstance().newXPath();
        Element element = (Element) xPath.evaluate(expression, doc, XPathConstants.NODE);
        return element.getAttribute("value");
    }

    private String getJID(ResultIQ iq) {
        Document doc = iq.getDocument();
        Element root = (Element) doc.getFirstChild();
        return root.getAttribute("from");
    }
}
