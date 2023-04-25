package Stanza;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;

public class ResultIQ extends Stanza {
  public ResultIQ(Document document) throws ParserConfigurationException {
    super(document);
  }

  public ResultIQ(String from, String to, String id) throws ParserConfigurationException {
    super();
    Element iq = this.getDocument().createElement("iq");
    iq.setAttribute("from", from);
    iq.setAttribute("to", to);
    iq.setAttribute("type", "result");
    iq.setAttribute("id", id);
    this.getDocument().appendChild(iq);
  }

  public void addItem(String info, String value) {
    Element root = (Element) this.getDocument().getFirstChild();
    Element item = this.getDocument().createElement("item");
    item.setAttribute("info", info);
    item.setAttribute("value", value);
    root.appendChild(item);
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    Element root = (Element) this.getDocument().getFirstChild();
    NodeList children = root.getChildNodes();
    result.append(
        String.format(
            "<%s from='%s' to='%s' type='%s' id='%s'>\n",
            root.getTagName(),
            root.getAttribute("from"),
            root.getAttribute("to"),
            root.getAttribute("type"),
            root.getAttribute("id")));

    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element ele = (Element) node;
        result.append(
            String.format(
                "\t<%s info='%s' value='%s'/>\n",
                ele.getTagName(), ele.getAttribute("info"), ele.getAttribute("value")));
      }
    }
    result.append(String.format("</%s>", root.getTagName()));
    return result.toString();
  }

  public static void main(String[] args) {
    try {
      ResultIQ iq = new ResultIQ("1", "2", "3");
      iq.addItem("temperature", "21");
      iq.addItem("humidity", "40");
      iq.addItem("brightness", "500");
      Stanza stanza = Stanza.getStanzaFromDocumentBytes(Stanza.getDocumentBytes(iq));
      System.out.println(stanza);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
