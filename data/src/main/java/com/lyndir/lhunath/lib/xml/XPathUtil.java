package com.lyndir.lhunath.lib.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Utilities for evaluating XPath on XHTML.
 *
 * @author mbillemo
 */
public class XPathUtil {

    private XPath xpath;

    /**
     * Create a new XPathUtil instance.
     */
    public XPathUtil(boolean isXHTML) {

        xpath = XPathFactory.newInstance().newXPath();

        if (isXHTML)
            xpath.setNamespaceContext( new XHTMLContext() );
    }

    /**
     * Evaluate an {@link XPath} expression and return the text from the selected nodes.
     *
     * @param context
     *        The context to evaluate the XPath expression under.
     * @param expressionFormat
     *        The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments
     *        The data used to satisfy the format parameters in the expressionFormat.
     */
    public Boolean getBoolean(Object context, String expressionFormat, Object... arguments)
            throws XPathExpressionException {

        return (Boolean) getObject( context, expressionFormat, XPathConstants.BOOLEAN, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression and return the first of the selected nodes.
     *
     * @param context
     *        The context to evaluate the XPath expression under.
     * @param expressionFormat
     *        The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments
     *        The data used to satisfy the format parameters in the expressionFormat.
     */
    public Node getNode(Object context, String expressionFormat, Object... arguments) throws XPathExpressionException {

        return (Node) getObject( context, expressionFormat, XPathConstants.NODE, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression and return a list of the selected nodes.
     *
     * @param context
     *        The context to evaluate the XPath expression under.
     * @param expressionFormat
     *        The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments
     *        The data used to satisfy the format parameters in the expressionFormat.
     */
    public List<Node> getNodes(Object context, String expressionFormat, Object... arguments)
            throws XPathExpressionException {

        List<Node> nodeList = new ArrayList<Node>();
        NodeList annoyingNodeList = (NodeList) getObject( context, expressionFormat, XPathConstants.NODESET, arguments );

        for (int node = 0; node < annoyingNodeList.getLength(); ++node)
            nodeList.add( annoyingNodeList.item( node ) );

        return nodeList;
    }

    /**
     * Evaluate an {@link XPath} expression and return the text from the selected nodes.
     *
     * @param context
     *        The context to evaluate the XPath expression under.
     * @param expressionFormat
     *        The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments
     *        The data used to satisfy the format parameters in the expressionFormat.
     */
    public Double getNumber(Object context, String expressionFormat, Object... arguments)
            throws XPathExpressionException {

        return (Double) getObject( context, expressionFormat, XPathConstants.NUMBER, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression and return the text from the selected nodes.
     *
     * @param context
     *        The context to evaluate the XPath expression under.
     * @param expressionFormat
     *        The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments
     *        The data used to satisfy the format parameters in the expressionFormat.
     */
    public String getString(Object context, String expressionFormat, Object... arguments)
            throws XPathExpressionException {

        return (String) getObject( context, expressionFormat, XPathConstants.STRING, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression and return the selected nodes as the given result type.
     *
     * @param context
     *        The context to evaluate the XPath expression under.
     * @param expressionFormat
     *        The XPath expression format. See {@link String#format(String, Object...)}.
     * @param result
     *        The type of result to return the selected nodes as.
     * @param arguments
     *        The data used to satisfy the format parameters in the expressionFormat.
     */
    private Object getObject(Object context, String expressionFormat, QName result, Object... arguments)
            throws XPathExpressionException {

        String expression = String.format( expressionFormat, arguments );

        if (context instanceof InputSource)
            return xpath.evaluate( expression, (InputSource) context, result );

        return xpath.evaluate( expression, context, result );
    }

    public class XHTMLContext implements NamespaceContext {

        private Map<String, String> namespaces = new HashMap<String, String>();

        /**
         * Create a new AuthDriver.XHTMLContext instance.
         */
        public XHTMLContext() {

            namespaces.put( XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI );
            namespaces.put( XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI );
            namespaces.put( XMLConstants.DEFAULT_NS_PREFIX, "http://www.w3.org/1999/xhtml" );
            namespaces.put( "xhtml", "http://www.w3.org/1999/xhtml" );
        }

        /**
         * @{inheritDoc}
         */
        public String getNamespaceURI(String prefix) {

            return namespaces.get( prefix );
        }

        /**
         * @{inheritDoc}
         */
        public String getPrefix(String namespaceURI) {

            for (Map.Entry<String, String> namespace : namespaces.entrySet())
                if (namespaceURI.equals( namespace.getValue() ))
                    return namespace.getKey();

            return null;
        }

        /**
         * @{inheritDoc}
         */
        public Iterator<String> getPrefixes(String namespaceURI) {

            List<String> URIs = new ArrayList<String>();
            for (Map.Entry<String, String> namespace : namespaces.entrySet())
                if (namespaceURI.equals( namespace.getValue() ))
                    URIs.add( namespace.getKey() );

            return URIs.iterator();
        }

    }
}
