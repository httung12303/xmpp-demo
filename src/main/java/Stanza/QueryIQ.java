package Stanza;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;

public class QueryIQ extends Stanza {
  public QueryIQ(Document document) throws ParserConfigurationException {
    super(document);
  }

  public QueryIQ(String from, String to, String id) throws ParserConfigurationException {
    super();
    Element iq = this.getDocument().createElement("iq");
    iq.setAttribute("from", from);
    iq.setAttribute("to", to);
    iq.setAttribute("type", "get");
    iq.setAttribute("id", id);
    this.getDocument().appendChild(iq);
  }

  public void addQuery(String info) {
    Element query = this.getDocument().createElement("query");
    query.setAttribute("info", info);
    Element iq = (Element) this.getDocument().getFirstChild();
    iq.appendChild(query);
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    Element root = (Element) this.getDocument().getFirstChild();
    NodeList children = root.getChildNodes();
    result.append(String.format(
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
        result.append(String.format("\t<%s info='%s'/>\n", ele.getTagName(), ele.getAttribute("info")));
      }
    }
    result.append(String.format("</%s>", root.getTagName()));
    return result.toString();
  }
}
