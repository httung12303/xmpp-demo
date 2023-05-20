package stanza;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ResultIQ extends Stanza {
    public ResultIQ(Document document) throws ParserConfigurationException {
        super(document);
        this.type = Stanza.RESULT_IQ;
    }

    public ResultIQ(String from, String to, String time) throws ParserConfigurationException {
        super();
        Element iq = this.getDocument().createElement("iq");
        iq.setAttribute("from", from);
        iq.setAttribute("to", to);
        iq.setAttribute("type", "result");
        iq.setAttribute("time", time);
        this.getDocument().appendChild(iq);
        this.type = Stanza.RESULT_IQ;
    }

    public void addItem(String info, String value) {
        Element root = (Element) this.getDocument().getFirstChild();
        Element item = this.getDocument().createElement("item");
        item.setAttribute("info", info);
        item.setAttribute("value", value);
        root.appendChild(item);
    }

    public String getInfo(String info) throws XPathExpressionException {
        Document doc = getDocument();
        String expression = String.format("//item[@info='%s']", info);
        XPath xPath = XPathFactory.newInstance().newXPath();
        Element element = (Element) xPath.evaluate(expression, doc, XPathConstants.NODE);
        return element.getAttribute("value");
    }
}
