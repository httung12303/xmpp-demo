package Data;
import java.io.StringReader;
import java.lang.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;

public class Stanza {
    private Document document;

    public Stanza(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }


    public Stanza() {
    }

    public Stanza(String jid, Time lastUpdate, float temerature, int humidity, int brightness, String status) throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        this.document = dBuilder.newDocument();
        Element rootElement = document.createElement("iq");
        document.appendChild(rootElement);

        Element jidElement = document.createElement("jid");
        jidElement.appendChild(document.createTextNode(jid));
        rootElement.appendChild(jidElement);

        Element lastUpdateElement = document.createElement("lastUpdate");
        lastUpdateElement.appendChild(document.createTextNode(lastUpdate.toString()));
        rootElement.appendChild(lastUpdateElement);

        Element temperatureElement = document.createElement("temperature");
        temperatureElement.appendChild(document.createTextNode(Float.toString(temerature)));
        rootElement.appendChild(temperatureElement);

        Element humidityElement = document.createElement("humidity");
        humidityElement.appendChild(document.createTextNode(Float.toString(humidity)));
        rootElement.appendChild(humidityElement);

        Element brightnessElement = document.createElement("brightness");
        brightnessElement.appendChild(document.createTextNode(Integer.toString(brightness)));
        rootElement.appendChild(brightnessElement);

        Element statusElement = document.createElement("status");
        statusElement.appendChild(document.createTextNode(status));
        rootElement.appendChild(statusElement);
    }

    public static byte[] getBytes(Stanza stanza) throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(stanza.getDocument());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        return outputStream.toByteArray();
    }
    public static Stanza receiveDocument(byte[] documentBytes) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(documentBytes);
        Document document = builder.parse(inputStream);
        return new Stanza(document);
    }

    public void saveToDatabase() {
        Element rootElement = document.getDocumentElement();
        String jid = rootElement.getElementsByTagName("jid").item(0).getTextContent();
        long lastUpdateElement = Time.parse(rootElement.getElementsByTagName("lastUpdate").item(0).getTextContent());
        float temerature = Float.parseFloat(rootElement.getElementsByTagName("temperature").item(0).getTextContent());
        int humidity = Integer.parseInt(rootElement.getElementsByTagName("humidity").item(0).getTextContent());
        int brightness = Integer.parseInt(rootElement.getElementsByTagName("brightness").item(0).getTextContent());
        String status = rootElement.getElementsByTagName("status").item(0).getTextContent();

        // The data is parsed
    }

    @Override
    public String toString() {
        return document.toString();
    }

    public static void main(String[] args) {
        Stanza stanza = null;
        try {
            stanza = new Stanza("123",new Time(12,15,30), (float)20, 45, 50, "Online");
            StringWriter
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        System.out.println(stanza.getDocument());
    }

}
