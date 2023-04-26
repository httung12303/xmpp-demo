package stanza;

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

abstract public class Stanza {
  public static final int QUERY_IQ = 0;
  public static final int RESULT_IQ = 1;
  protected int type;
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

  public int getType() {
    return this.type;
  }
}
