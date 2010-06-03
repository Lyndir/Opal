package com.lyndir.lhunath.lib.xml;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Utilities for evaluating XPath on XHTML.
 *
 * @author mbillemo
 */
public class XPathUtil {

    private final XPath xpath;


    /**
     * Create a new XPathUtil instance.
     *
     * @param isXHTML <code>true</code>: Indicate that the document uses the XHTML namespace context and set it as the
     *                default context.
     */
    public XPathUtil(final boolean isXHTML) {

        xpath = XPathFactory.newInstance().newXPath();

        if (isXHTML)
            xpath.setNamespaceContext( new XHTMLContext() );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The text from the selected nodes
     *
     * @throws XPathExpressionException
     */
    public Boolean getBoolean(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        return (Boolean) getObject( context, expressionFormat, XPathConstants.BOOLEAN, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The first of the selected nodes.
     *
     * @throws XPathExpressionException
     */
    public Node getNode(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        return (Node) getObject( context, expressionFormat, XPathConstants.NODE, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return A list of the selected nodes.
     *
     * @throws XPathExpressionException
     */
    public List<Node> getNodes(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        List<Node> nodeList = new ArrayList<Node>();
        NodeList annoyingNodeList = (NodeList) getObject( context, expressionFormat, XPathConstants.NODESET,
                                                          arguments );

        for (int node = 0; node < annoyingNodeList.getLength(); ++node)
            nodeList.add( annoyingNodeList.item( node ) );

        return nodeList;
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The text from the selected nodes.
     *
     * @throws XPathExpressionException
     */
    public Double getNumber(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        return (Double) getObject( context, expressionFormat, XPathConstants.NUMBER, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The text from the selected nodes.
     *
     * @throws XPathExpressionException
     */
    public String getString(final Object context, final String expressionFormat, final Object... arguments)
            throws XPathExpressionException {

        return (String) getObject( context, expressionFormat, XPathConstants.STRING, arguments );
    }

    /**
     * Evaluate an {@link XPath} expression.
     *
     * @param context          The context to evaluate the XPath expression under.
     * @param expressionFormat The XPath expression format. See {@link String#format(String, Object...)}.
     * @param result           The type of result to return the selected nodes as.
     * @param arguments        The data used to satisfy the format parameters in the expressionFormat.
     *
     * @return The selected nodes as the given result type.
     *
     * @throws XPathExpressionException If the given expression is invalid or does not match the context.
     */
    private Object getObject(Object context, final String expressionFormat, final QName result, Object... arguments)
            throws XPathExpressionException {

        String expression = String.format( expressionFormat, arguments );

        if (context instanceof InputSource)
            return xpath.evaluate( expression, (InputSource) context, result );

        return xpath.evaluate( expression, context, result );
    }


    /**
     * <h2>{@link XHTMLContext}<br>
     * <sub>Namespace context for XHTML tags and attributes.</sub></h2>
     *
     * <p>
     * <i>Apr 9, 2008</i>
     * </p>
     *
     * @author mbillemo
     */
    private class XHTMLContext implements NamespaceContext {

        private final Map<String, String> namespaces = new HashMap<String, String>();


        /**
         * Create a new AuthDriver.XHTMLContext instance.
         */
        XHTMLContext() {

            namespaces.put( XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI );
            namespaces.put( XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI );
            namespaces.put( XMLConstants.DEFAULT_NS_PREFIX, "http://www.w3.org/1999/xhtml" );
            namespaces.put( "xhtml", "http://www.w3.org/1999/xhtml" );
        }

        /**
         * {@inheritDoc}
         *
         * @param prefix The prefix of the namespace to retrieve.
         *
         * @return The namespace mapped by the given prefix.
         */
        @Override
        public String getNamespaceURI(final String prefix) {

            return namespaces.get( prefix );
        }

        /**
         * {@inheritDoc}
         *
         * @param namespaceURI The namespace to retrieve the first set prefix for.
         *
         * @return The first prefix that maps the given namespace.
         */
        @Override
        public String getPrefix(final String namespaceURI) {

            for (final Map.Entry<String, String> namespace : namespaces.entrySet())
                if (namespaceURI.equals( namespace.getValue() ))
                    return namespace.getKey();

            return null;
        }

        /**
         * {@inheritDoc}
         *
         * @param namespaceURI The namespace to retrieve the prefixes for.
         *
         * @return All prefixes that map the given namespace.
         */
        @Override
        public Iterator<String> getPrefixes(final String namespaceURI) {

            List<String> uris = new ArrayList<String>();
            for (final Map.Entry<String, String> namespace : namespaces.entrySet())
                if (namespaceURI.equals( namespace.getValue() ))
                    uris.add( namespace.getKey() );

            return uris.iterator();
        }

    }
}
