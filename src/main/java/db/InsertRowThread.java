package db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import stanza.ResultIQ;
import stanza.Stanza;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class InsertRowThread extends Thread{
    private ResultIQ iq;
    private DBManager db;
    private long timeReceived;
    public InsertRowThread(ResultIQ iq, DBManager db, long timeReceived) {
        this.timeReceived = timeReceived;
        this.db = db;
        this.iq = iq;
    }
    @Override
    public void run() {
        try {
            String jid = iq.getSender();
            String time = iq.getTime();
            float temperature = Float.parseFloat(iq.getInfo("temperature"));
            int humidity = Integer.parseInt(iq.getInfo("humidity"));
            int brightness = Integer.parseInt(iq.getInfo("brightness"));
            long delay = timeReceived - iq.getTimeSent();
            float goodput = delay == 0 ? (float) 8000 : (float) (Stanza.getDocumentBytes(iq).length * 1000) / delay;
            synchronized (db) {
                db.insertIntoClientsTable(jid, time, temperature, humidity, brightness, delay, goodput);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
