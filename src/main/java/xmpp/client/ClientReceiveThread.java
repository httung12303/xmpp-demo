package xmpp.client;

import environment.RecEnviroment;
import iothread.ReceiveThread;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import socketwrapper.SocketWrapper;
import stanza.ResultIQ;
import stanza.Stanza;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;

public class ClientReceiveThread extends ReceiveThread {

    private RecEnviroment recEnviroment;
    public ClientReceiveThread(SocketWrapper wrapper, RecEnviroment recEnviroment) throws IOException {
        super(wrapper);
    }

    public void processStanza(Stanza stanza) {
//        if(stanza.getType() == Stanza.RESULT_IQ) {
//            processResultIQ((ResultIQ) stanza);
//        }
        System.out.println(stanza.toString());
    }

    public void processResultIQ(ResultIQ iq) {
        try {
            float temp = Float.valueOf(getInfo(iq, "temperature"));
            int humidity = Integer.valueOf(getInfo(iq, "humidity"));
            int brightness = Integer.valueOf(getInfo(iq, "brightness"));
            recEnviroment.update(temp, humidity, brightness);
        }
        catch (Exception e) {
        }
    }

    private String getInfo(ResultIQ iq, String info) throws XPathExpressionException {
        Document doc = iq.getDocument();
        String expression = String.format("//item[@info='%s']", info);
        XPath xPath = XPathFactory.newInstance().newXPath();
        Element element = (Element) xPath.evaluate(expression, doc, XPathConstants.NODE);
        return element.getAttribute("value");
    }
}
