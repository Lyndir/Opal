/*
 *   Copyright 2005-2007 Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.lib.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <h2>{@link Structure}<br>
 * <sub>A utility class to create XML document builders.</sub></h2>
 * <p>
 * Use {@link #getTidyBuilder()} to build an XHTML document builder from an HTML stream.<br>
 * <br>
 * Either use {@link #getXMLBuilder()}, {@link #getXMLBuilder(Schema)} or
 * {@link #getXMLBuilder(boolean, boolean, boolean, boolean, boolean, boolean, boolean, Schema)} to parse XML streams.<br>
 * <br>
 * Use {@link #toString(Node)} to render an XML node or document as a string.
 * </p>
 * <p>
 * <i>Apr 8, 2008</i>
 * </p>
 * 
 * @author mbillemo
 */
public class Structure {

    private static final int TAB_SIZE = 4;


    /**
     * @return a builder that can parse HTML data.
     */
    public static Tidy getTidyBuilder() {

        Tidy factory = new Tidy();
        factory.setXmlOut( true );
        factory.setQuiet( true );

        return factory;
    }

    /**
     * The builder created by this method operates as follows:
     * <ul>
     * <li>Do not convert CDATA to text nodes.</li>
     * <li>Expand entity references.</li>
     * <li>Do not ignore comment nodes.</li>
     * <li>Do not ignore white space.</li>
     * <li>Is not namespace-aware.</li>
     * <li>Is not XInclude-aware.</li>
     * <li>Is not validating.</li>
     * </ul>
     * 
     * @return a builder that parses XML data according to the defaults highlighted above.
     */
    public static DocumentBuilder getXMLBuilder() {

        return getXMLBuilder( false, true, false, false, false, false, false, null );
    }

    /**
     * The builder created by this method operates as follows:
     * <ul>
     * <li>Do not convert CDATA to text nodes.</li>
     * <li>Expand entity references.</li>
     * <li>Do not ignore comment nodes.</li>
     * <li>Do not ignore white space.</li>
     * <li>Is not namespace-aware.</li>
     * <li>Is not XInclude-aware.</li>
     * <li>Is validating using the given schema.</li>
     * </ul>
     * 
     * @param schema
     *            The schema to validate against.
     * @return a builder that parses XML data according to the defaults highlighted above.
     */
    public static DocumentBuilder getXMLBuilder(Schema schema) {

        return getXMLBuilder( false, true, false, false, false, false, true, schema );
    }

    /**
     * @param coalescing
     *            <code>true</code> to convert CDATA to text nodes.
     * @param expandEntityRef
     *            <code>true</code> to expand entity reference nodes.
     * @param ignoreComments
     *            <code>true</code> to ignore comment nodes.
     * @param whitespace
     *            <code>true</code> to remove 'ignorable whitespace'.
     * @param awareness
     *            <code>true</code> to be namespace-aware.
     * @param xIncludes
     *            <code>true</code> to be XInclude-aware.
     * @param validating
     *            <code>true</code> to validate the XML data against a schema.
     * @param schema
     *            The schema to validate against. Specify <code>null</code> if not validating.
     * @return a builder that parses XML data according to the rules specified by the arguments.
     */
    public static DocumentBuilder getXMLBuilder(boolean coalescing, boolean expandEntityRef, boolean ignoreComments,
            boolean whitespace, boolean awareness, boolean xIncludes, boolean validating, Schema schema) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setCoalescing( coalescing );
            factory.setExpandEntityReferences( expandEntityRef );
            factory.setIgnoringComments( ignoreComments );
            factory.setIgnoringElementContentWhitespace( whitespace );
            factory.setNamespaceAware( awareness );
            factory.setXIncludeAware( xIncludes );
            factory.setValidating( validating );
            factory.setSchema( schema );

            return factory.newDocumentBuilder();
        }

        catch (ParserConfigurationException e) {
            Logger.error( e, "Document Builder has not been configured correctly!" );
        }

        return null;
    }

    /**
     * This method trims text node data.
     * 
     * @param node
     *            The XML node to render.
     * @return the given node as an XML-formatted string.
     */
    public static String toString(Node node) {

        return toString( node, true );
    }

    /**
     * @param node
     *            The XML node to render.
     * @param trim
     *            <code>false</code>: Don't trim whitespace from text node data.
     * @return the given node as an XML-formatted string.
     */
    public static String toString(Node node, boolean trim) {

        StringBuffer result = toString( node, 1, trim );
        return result == null ? null : result.toString().replaceFirst( "\n$", "" );
    }

    private static StringBuffer toString(Node node, int indent, boolean trim) {

        if (node == null)
            return null;

        if (node.getNodeType() == Node.TEXT_NODE)
            return new StringBuffer( indent( indent ) ).append( trim ? node.getNodeValue().trim() : node.getNodeValue() ).append(
                    '\n' );

        StringBuffer out = new StringBuffer();
        out.append( indent( indent ) );
        out.append( '<' ).append( node.getNodeName() );

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node attribute = attributes.item( i );

            out.append( ' ' ).append( attribute.getNodeName() );
            out.append( '=' ).append( '"' ).append( attribute.getNodeValue() ).append( '"' );
        }

        out.append( '>' ).append( '\n' );

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item( i );

            out.append( toString( child, indent + 1, trim ) );
        }

        String value = node.getNodeValue();
        if (value != null && value.length() > 0) {
            out.append( indent( indent + 1 ) );
            out.append( value ).append( '\n' );
        }

        out.append( indent( indent ) );
        out.append( '<' ).append( '/' ).append( node.getNodeName() ).append( '>' ).append( '\n' );

        return out;
    }

    private static String indent(int indent) {

        return String.format( "%" + indent * TAB_SIZE + "s", "" );
    }
}
