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
            String jid = getJID(iq);
            String time = getInfo(iq, "time");
            String temperature = getInfo(iq, "temperature");
            String humidity = getInfo(iq, "humidity");
            String brightness = getInfo(iq, "brightness");
            String last_update = String.valueOf(LocalTime.now());
            synchronized (db) {
                db.insertIntoClientsTable(jid, time, temperature, humidity, brightness, last_update);
            }
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
