package Stanza;

import java.lang.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Stanza {
  private Document document;

  public Stanza(Document document) {
    this.document = document;
  }

  public Stanza() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    this.document = builder.newDocument();
  }

  public Document getDocument() {
    return document;
  }

  public static byte[] getDocumentBytes(Stanza stanza) throws TransformerException {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    transformer.transform(new DOMSource(stanza.getDocument()), new StreamResult(outputStream));
    return outputStream.toByteArray();
  }

  public static Stanza getStanzaFromDocumentBytes(byte[] documentBytes)
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(documentBytes);
    Document document = builder.parse(inputStream);

    Element root = (Element) document.getFirstChild();
    String rootTag = root.getTagName();
    if (rootTag.equals("iq")) {
      String type = root.getAttribute("type");
      if (type.equals("get")) {
        return new QueryIQ(document);
      } else {
        return new ResultIQ(document);
      }
    }
    return null;
  }

  public static void main(String[] args) {
    try {
      QueryIQ iq = new QueryIQ("1", "2", "0205");
      iq.addQuery("temp");
      iq.addQuery("humid");
      Stanza otherIQ = Stanza.getStanzaFromDocumentBytes(Stanza.getDocumentBytes(iq));
      System.out.println(iq.toString());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
