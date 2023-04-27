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
            String temperature = iq.getInfo("temperature");
            String humidity = iq.getInfo("humidity");
            String brightness = iq.getInfo("brightness");
            String delay = String.valueOf(timeReceived - iq.getTimeSent());
            synchronized (db) {
                db.insertIntoClientsTable(jid, time, temperature, humidity, brightness, delay);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
