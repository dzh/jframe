/**
 * 
 */
package jframe.pay.domain.util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dzh
 * @date Oct 21, 2015 3:53:35 PM
 * @since 1.0
 */
public class XmlUtil {

    public static Map<String, String> fromXml(String content) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document source = builder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));

        NodeList nodeList = source.getDocumentElement().getChildNodes();

        Map<String, String> map = new HashMap<>();
        Node node = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            map.put(node.getNodeName(), node.getTextContent());
        }
        return map;
    }

    public static String toXml(String root, Map<String, String> body) {
        if (body == null)
            return "";

        return body.keySet().stream().map(key -> {
            return "<" + key + ">" + body.get(key) + "</" + key + ">";
        }).collect(Collectors.joining("", "<" + root + ">", "</" + root + ">"));

    }

}
