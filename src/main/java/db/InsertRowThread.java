package db;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import stanza.ResultIQ;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.time.LocalTime;

public class InsertRowThread extends Thread{
    private ResultIQ iq;
    private DBManager db;
    public InsertRowThread(ResultIQ iq, DBManager db) {
        this.db = db;
        this.iq = iq;
    }
    @Override
    public void run() {
        try {
            String jid = ResultIQ.getJID(iq);
            String time = ResultIQ.getInfo(iq, "time");
            String temperature = ResultIQ.getInfo(iq, "temperature");
            String humidity = ResultIQ.getInfo(iq, "humidity");
            String brightness = ResultIQ.getInfo(iq, "brightness");
            String last_update = String.valueOf(LocalTime.now());
            synchronized (db) {
                db.insertIntoClientsTable(jid, time, temperature, humidity, brightness, last_update);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
