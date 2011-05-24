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
package com.lyndir.lhunath.opal.xml;

import com.google.common.base.Throwables;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.parsers.*;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.*;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;


/**
 * <h2>{@link Structure}<br> <sub>A utility class to create XML document builders.</sub></h2> <p> Use {@link #getTidyBuilder()} to build an
 * XHTML document builder from an HTML stream.<br> <br> Either use {@link #getXMLBuilder()}, {@link #getXMLBuilder(Schema)} or {@link
 * #getXMLBuilder(boolean, boolean, boolean, boolean, boolean, boolean, boolean, Schema)} to parse XML streams.<br> <br> Use {@link
 * #toString(Node)} to render an XML node or document as a string. </p> <p> <i>Apr 8, 2008</i> </p>
 *
 * @author mbillemo
 */
public class Structure {

    private static final Logger logger = Logger.get( Structure.class );

    private static final XPathUtil xmlpath = new XPathUtil( false );
    // private static final XPathUtil xhtmlpath = new XPathUtil( true );

    private static final int     TAB_SIZE = 4;
    private static final Pattern LINE     = Pattern.compile( "\n$" );

    private static <T> void inject(final Node root, final T structure)
            throws XPathExpressionException {

        // Inject the XML data into the XInject fields.
        for (final Field field : structure.getClass().getDeclaredFields()) {
            XInject annotation = field.getAnnotation( XInject.class );
            if (annotation == null)
                continue;

            try {
                Class<?> valueType = field.getType();
                field.setAccessible( true );

                logger.dbg(
                        "Setting (%s) '%s' to '%s' (xpath: %s)", valueType.getSimpleName(), field.getName(),
                        xmlpath.getString( root, annotation.value() ), annotation.value() );

                Object value;
                if (Byte.class.isAssignableFrom( valueType ) || Byte.TYPE.isAssignableFrom( valueType ))
                    value = xmlpath.getNumber( root, annotation.value() ).byteValue();

                else if (Double.class.isAssignableFrom( valueType ) || Double.TYPE.isAssignableFrom( valueType ))
                    // noinspection UnnecessaryUnboxing
                    value = xmlpath.getNumber( root, annotation.value() ).doubleValue();

                else if (Float.class.isAssignableFrom( valueType ) || Float.TYPE.isAssignableFrom( valueType ))
                    value = xmlpath.getNumber( root, annotation.value() ).floatValue();

                else if (Integer.class.isAssignableFrom( valueType ) || Integer.TYPE.isAssignableFrom( valueType ))
                    value = xmlpath.getNumber( root, annotation.value() ).intValue();

                else if (Long.class.isAssignableFrom( valueType ) || Long.TYPE.isAssignableFrom( valueType ))
                    value = xmlpath.getNumber( root, annotation.value() ).longValue();

                else if (Short.class.isAssignableFrom( valueType ) || Short.TYPE.isAssignableFrom( valueType ))
                    value = xmlpath.getNumber( root, annotation.value() ).shortValue();

                else if (Boolean.class.isAssignableFrom( valueType ) || Boolean.TYPE.isAssignableFrom( valueType ))
                    value = xmlpath.getBoolean( root, annotation.value() );

                else
                    value = xmlpath.getString( root, annotation.value() );

                field.set( structure, value );
            }

            // In case data to the field goes wrong (shouldn't).
            catch (IllegalArgumentException e) {
                logger.err( e, "Unexpected data type." );
            }
            catch (IllegalAccessException e) {
                logger.err( e, "Field not accessible." );
            }
        }

        // Look for XAfterInject methods.
        for (final Method method : structure.getClass().getDeclaredMethods())
            if (method.getAnnotation( XAfterInject.class ) != null)
                try {
                    method.setAccessible( true );
                    method.invoke( structure );
                }

                catch (IllegalArgumentException e) {
                    logger.err( e, "XAfterInject method shouldn't take any arguments." );
                }
                catch (IllegalAccessException e) {
                    logger.err( e, "XAfterInject method must be accessible." );
                }
                catch (InvocationTargetException e) {
                    logger.err( e, "XAfterInject method throw an exception." );
                }
    }

    /**
     * Load XML data into an object that has the {@link FromXML} annotation on it.
     *
     * @param <T>  The type of the class instance to inject XML data into.
     * @param type The annotated class to create an object for.
     *
     * @return An object of the given type with XML data injected.
     *
     * @throws IOException              If any IO errors occur.
     * @throws SAXException             If any parse errors occur.
     * @throws XPathExpressionException Field injection annotations specified XPath expressions that failed to evaluate on the source data.
     */
    public static <T> T load(final Class<T> type)
            throws IOException, SAXException, XPathExpressionException {

        // Test whether the given type actually has a FromXML annotation on it.
        if (type.getAnnotation( FromXML.class ) == null)
            throw new IllegalArgumentException( "Object passed must have the FromXML annotation." );

        // Create an empty object of the specified type.
        T structure;
        try {
            structure = type.getConstructor().newInstance();
        }
        catch (InstantiationException e) {
            logger.bug( e );
            throw Throwables.propagate( e );
        }
        catch (IllegalAccessException e) {
            logger.bug( e );
            throw Throwables.propagate( e );
        }
        catch (NoSuchMethodException e) {
            logger.bug( e );
            throw Throwables.propagate( e );
        }
        catch (InvocationTargetException e) {
            throw Throwables.propagate( e );
        }

        // Set up our XML parser.
        DocumentBuilder builder = getXMLBuilder();
        String resourceName = type.getAnnotation( FromXML.class ).value();

        // Parse in our XML data.
        Element root = builder.parse( ClassLoader.getSystemResourceAsStream( resourceName ) ).getDocumentElement();

        inject( root, structure );

        return structure;
    }

    /**
     * Load XML data into a list of objects that have the {@link FromXML} annotation on it.
     *
     * @param <T>  The type of the class instance to inject XML data into.
     * @param type The annotated class to create a objects for.
     *
     * @return A list of object of the given type with XML data injected.
     *
     * @throws IOException              If any IO errors occur.
     * @throws SAXException             If any parse errors occur.
     * @throws XPathExpressionException Field injection annotations specified XPath expressions that failed to evaluate on the source data.
     */
    public static <T> List<T> loadAll(final Class<T> type)
            throws IOException, SAXException, XPathExpressionException {

        // Test whether the given type actually has a FromXML annotation on it.
        if (type.getAnnotation( FromXML.class ) == null)
            throw new IllegalArgumentException( "Object passed must have the FromXML annotation." );

        // Set up our XML parser.
        DocumentBuilder builder = getXMLBuilder();
        String resourceName = type.getAnnotation( FromXML.class ).value();

        // Parse in our XML data.
        Element root = builder.parse( ClassLoader.getSystemResourceAsStream( resourceName ) ).getDocumentElement();

        // Create an empty object of the specified type.
        List<Node> children = xmlpath.getNodes( root, "/*/*" );
        List<T> structures = new ArrayList<T>( children.size() );
        for (final Node child : children) {
            T structure;
            try {
                structure = type.getConstructor().newInstance();
            }
            catch (InstantiationException e) {
                throw logger.bug( e );
            }
            catch (IllegalAccessException e) {
                throw logger.bug( e );
            }
            catch (NoSuchMethodException e) {
                throw logger.bug( e );
            }
            catch (InvocationTargetException e) {
                throw Throwables.propagate( e );
            }

            // If an XInjectTag is defined; set it to the name of the child tag.
            for (final Field field : structure.getClass().getDeclaredFields())
                if (field.isAnnotationPresent( XInjectTag.class ))
                    try {
                        logger.dbg(
                                "Setting (%s) '%s' to tagname '%s'", field.getType().getSimpleName(), field.getName(),
                                child.getNodeName() );

                        field.setAccessible( true );
                        field.set( structure, child.getNodeName() );
                    }

                    catch (IllegalArgumentException e) {
                        logger.err( e, "XInjectTag field of the wrong type." );
                    }
                    catch (IllegalAccessException e) {
                        logger.err( e, "XInjectTag field not accessible." );
                    }

            // Inject the child tag's data into our new structure object.
            inject( child, structure );
            structures.add( structure );
        }

        return structures;
    }

    /**
     * Convert an object that has the {@link FromXML} annotation to an XML structured string.
     *
     * @param structure The object to render as XML.
     *
     * @return the given object as an XML-formatted string.
     */
    public static String toString(final Object structure) {

        if (structure.getClass().getAnnotation( FromXML.class ) == null)
            throw new IllegalArgumentException( "Object passed must have the FromXML annotation." );

        StringBuilder out = new StringBuilder( String.format( "<%s>\n", structure.getClass().getSimpleName() ) );
        for (final Field field : structure.getClass().getDeclaredFields()) {
            XInject annotation = field.getAnnotation( XInject.class );
            if (annotation == null)
                continue;

            try {
                field.setAccessible( true );
                out.append( indent( 1 ) ).append( '<' ).append( annotation.value() );
                String attribute = annotation.attribute();
                if (attribute.length() > 0)
                    out.append( String.format( " %s=\"%s\"", attribute, field.get( structure ).toString() ) );

                String content = String.valueOf( field.get( structure ) );
                if (content == null || content.length() == 0 || attribute.length() > 0)
                    out.append( ' ' ).append( '/' ).append( '>' ).append( '\n' );
                else {
                    out.append( '>' ).append( '\n' );
                    out.append( indent( 2 ) ).append( content ).append( '\n' );
                    out.append( indent( 1 ) ).append( String.format( "</%s>\n", annotation.value() ) );
                }
            }

            catch (IllegalArgumentException ignored) {
            }
            catch (IllegalAccessException ignored) {
            }
        }
        out.append( String.format( "</%s>", structure.getClass().getSimpleName() ) );

        return out.toString();
    }

    /**
     * @return a builder that can parse HTML data.
     */
    public static Tidy getTidyBuilder() {

        Tidy factory = new Tidy();
        factory.setXHTML( true );
        factory.setQuiet( true );
        factory.setOnlyErrors( true );
        factory.setShowWarnings( false );

        return factory;
    }

    /**
     * The builder created by this method operates as follows: <ul> <li>Do not convert CDATA to text nodes.</li> <li>Expand entity
     * references.</li> <li>Do not ignore comment nodes.</li> <li>Do not ignore white space.</li> <li>Is not namespace-aware.</li> <li>Is
     * not XInclude-aware.</li> <li>Is not validating.</li> </ul>
     *
     * @return a builder that parses XML data according to the defaults highlighted above.
     */
    public static DocumentBuilder getXMLBuilder() {

        return getXMLBuilder( false, true, false, false, false, false, false, null );
    }

    /**
     * The builder created by this method operates as follows: <ul> <li>Do not convert CDATA to text nodes.</li> <li>Expand entity
     * references.</li> <li>Do not ignore comment nodes.</li> <li>Do not ignore white space.</li> <li>Is not namespace-aware.</li> <li>Is
     * not XInclude-aware.</li> <li>Is validating using the given schema.</li> </ul>
     *
     * @param schema The schema to validate against.
     *
     * @return a builder that parses XML data according to the defaults highlighted above.
     */
    public static DocumentBuilder getXMLBuilder(final Schema schema) {

        return getXMLBuilder( false, true, false, false, false, false, true, schema );
    }

    /**
     * @param coalescing      <code>true</code> to convert CDATA to text nodes.
     * @param expandEntityRef <code>true</code> to expand entity reference nodes.
     * @param ignoreComments  <code>true</code> to ignore comment nodes.
     * @param whitespace      <code>true</code> to remove 'ignorable whitespace'.
     * @param awareness       <code>true</code> to be namespace-aware.
     * @param xIncludes       <code>true</code> to be XInclude-aware.
     * @param validating      <code>true</code> to validate the XML data against a schema.
     * @param schema          The schema to validate against. Specify <code>null</code> if not validating.
     *
     * @return a builder that parses XML data according to the rules specified by the arguments.
     */
    public static DocumentBuilder getXMLBuilder(final boolean coalescing, final boolean expandEntityRef, final boolean ignoreComments,
                                                final boolean whitespace, final boolean awareness, final boolean xIncludes,
                                                final boolean validating, final Schema schema) {

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
            logger.err( e, "Document Builder has not been configured correctly!" );
        }

        return null;
    }

    /**
     * This method trims text node data.
     *
     * @param node The XML node to render.
     *
     * @return the given node as an XML-formatted string.
     */
    public static String toString(final Node node) {

        return toString( node, true );
    }

    /**
     * @param node The XML node to render.
     * @param trim <code>false</code>: Don't trim whitespace from text node data.
     *
     * @return the given node as an XML-formatted string.
     */
    public static String toString(final Node node, final boolean trim) {

        StringBuffer result = toString( node, 1, trim );
        return result == null? null: LINE.matcher( result.toString() ).replaceFirst( "" );
    }

    private static StringBuffer toString(final Node node, final int indent, final boolean trim) {

        if (node == null)
            return null;

        if (node.getNodeType() == Node.TEXT_NODE) {
            String nodeValue = node.getNodeValue();
            if (trim)
                nodeValue = nodeValue.trim();

            return new StringBuffer( indent( indent ) ).append( nodeValue.trim() ).append( '\n' );
        }

        StringBuffer out = new StringBuffer();
        out.append( indent( indent ) );
        out.append( '<' ).append( node.getNodeName() );

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null)
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

    private static String indent(final int indent) {

        return String.format( "%" + indent * TAB_SIZE + 's', "" );
    }
}
