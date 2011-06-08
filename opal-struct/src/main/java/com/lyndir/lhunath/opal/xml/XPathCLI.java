/*
 *   Copyright 2008, Maarten Billemont
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

import java.io.*;
import java.util.Arrays;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * <h2>{@link XPathCLI}<br> <sub>CLI utility to test and execute XPath expressions on XML/XHTML/HTML data.</sub></h2>
 *
 * <p> <i>Jun 10, 2008</i> </p>
 *
 * @author mbillemo
 */
public class XPathCLI {

    private static final Logger logger = LoggerFactory.getLogger( XPathCLI.class );

    /**
     * Entry point of the command-line XPath utility.
     *
     * @param args Command-line arguments to the utility.
     *
     * @throws IOException              If any IO errors occur.
     * @throws SAXException             If any parse errors occur.
     * @throws XPathExpressionException The specified XPath expression failed to evaluate on the source data.
     */
    public static void main(final String[] args)
            throws SAXException, IOException, XPathExpressionException {

        InputStream in = System.in;
        String xpathExpression = null;

        /* Arguments. */
        boolean fileArg = false, expression = false, xhtml = false, tidy = false, value = false, trim = true;
        for (final String arg : Arrays.asList( args ))
            if ("-f".equals( arg ) || "--file".equals( arg ))
                fileArg = true;
            else if (fileArg) {
                //noinspection IOResourceOpenedButNotSafelyClosed
                in = new FileInputStream( arg );
                fileArg = false;
            } else if ("-e".equals( arg ) || "--expression".equals( arg ))
                expression = true;
            else if (expression) {
                xpathExpression = arg;
                expression = false;
            } else if ("-x".equals( arg ) || "--xhtml".equals( arg ))
                tidy = xhtml = true;

            else if ("-t".equals( arg ) || "--tidy".equals( arg ))
                tidy = true;

            else if ("-v".equals( arg ) || "--value".equals( arg ))
                value = true;

            else if ("-T".equals( arg ) || "--no-trim".equals( arg ))
                trim = false;

            else if ("-h".equals( arg ) || "--help".equals( arg )) {
                System.out.println();
                System.out.println( "\tXPath Utility" );
                System.out.println( "\t\tlhunath" );

                System.out.println();
                System.out.println( "\t-f | --file [filename]" );
                System.out.println(
                        "\t\tSpecifies the file to open as XML data source." + "\n\t\tIf not specified, will use standard input." );

                System.out.println();
                System.out.println( "\t-e | --expression [xpath-expression]" );
                System.out
                        .println( "\t\tSpecifies the XPath expression to perform onto" + "\n\t\tthe XML data in order to select a node." );

                System.out.println();
                System.out.println( "\t-x | --xhtml" );
                System.out.println(
                        "\t\tSets the XHTML namespace as the default so you" + "\n\t\tneedn't specify it explicitly."
                        + "\n\t\tThis implicitly enables `-t`; see below." );

                System.out.println();
                System.out.println( "\t-t | --tidy" );
                System.out.println(
                        "\t\tUse Tidy as the XML document parser.  Tidy can" + "\n\t\tovercome certain problems with (X)HTML data." );

                System.out.println();
                System.out.println( "\t-v | --value" );
                System.out.println( "\t\tOutput the string value of the selected node." );

                System.out.println();
                System.out.println( "\t-T | --no-trim" );
                System.out.println( "\t\tTrim leading and trailing whitespace from text" + "\n\t\tnodes.  This has no effect with `-v`." );

                System.out.println();
                return;
            } else {
                logger.error( "'%s' is not a valid argument.", arg );
                System.exit( 1 );
            }
        if (xpathExpression == null) {
            logger.error( "No XPath Expression provided." );
            System.exit( 1 );
        }

        /* XPath Setup (use XHTML namespace?). */
        XPathUtil xpath = new XPathUtil( xhtml );

        /* Parse Data Source (pick the right parser for the job). */
        Document document;
        if (tidy)
            document = Structure.getTidyBuilder().parseDOM( in, null );
        else
            document = Structure.getXMLBuilder().parse( in );

        /* Execute the XPath expression. */
        String result;
        if (value)
            result = xpath.getString( document, xpathExpression );
        else
            result = Structure.toString( xpath.getNode( document, xpathExpression ), trim );

        /* Output result and set exit code if no result found. */
        if (result != null)
            System.out.println( result );
        else
            System.exit( 1 );
    }
}
