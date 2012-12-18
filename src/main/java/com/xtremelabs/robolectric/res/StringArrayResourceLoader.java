package com.xtremelabs.robolectric.res;

import android.content.res.Resources;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringArrayResourceLoader extends XpathResourceXmlLoader {
    Map<String, Value[]> stringArrayValues = new HashMap<String, Value[]>();
    private StringResourceLoader stringResourceLoader;

    public StringArrayResourceLoader(ResourceExtractor resourceExtractor, StringResourceLoader stringResourceLoader) {
        super(resourceExtractor, "/resources/string-array");
        this.stringResourceLoader = stringResourceLoader;
    }

    public String[] getArrayValue(int resourceId) {
        String resourceName = resourceExtractor.getResourceName(resourceId);
        Value[] values = stringArrayValues.get(resourceName);
        if (values == null) throw new Resources.NotFoundException(resourceName);
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            Value value = values[i];
            if (value.text.startsWith("@")) {
                result[i] = stringResourceLoader.getValue(value.text.substring(1), value.packageName);
            } else {
                result[i] = value.text;
            }
        }
        return result;
    }

    @Override protected void processNode(Node node, String name, XmlContext xmlContext) throws XPathExpressionException {
        XPathExpression itemXPath = XPathFactory.newInstance().newXPath().compile("item");
        NodeList childNodes = (NodeList) itemXPath.evaluate(node, XPathConstants.NODESET);
        List<Value> arrayValues = new ArrayList<Value>();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            arrayValues.add(new Value(childNode.getTextContent(), xmlContext.packageName));
        }
        String valuePointer = xmlContext.packageName + ":array/" + name;
        stringArrayValues.put(valuePointer, arrayValues.toArray(new Value[arrayValues.size()]));
    }

    private static class Value {
        String text;
        String packageName;

        private Value(String text, String packageName) {
            this.text = text;
            this.packageName = packageName;
        }
    }
}
