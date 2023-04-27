package xmpp.client;

import environment.Environment;
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
    private Environment environment;
    public ClientReceiveThread(SocketWrapper wrapper, Environment environment) throws IOException {
        super(wrapper);
        this.environment = environment;
    }

    public void processStanza(Stanza stanza) {
        if(stanza.getType() == Stanza.RESULT_IQ) {
            processResultIQ((ResultIQ) stanza);
        }
    }

    public void processResultIQ(ResultIQ iq) {
        try {
            float temp = Float.parseFloat(getInfo(iq, "temperature"));
            int humidity = Integer.parseInt(getInfo(iq, "humidity"));
            int brightness = Integer.parseInt(getInfo(iq, "brightness"));
            environment.update(temp, humidity, brightness);
        }
        catch (Exception ignored) {
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
