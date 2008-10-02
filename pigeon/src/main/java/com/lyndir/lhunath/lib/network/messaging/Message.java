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
package com.lyndir.lhunath.lib.network.messaging;

import com.lyndir.lhunath.lib.system.logging.Logger;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;


/**
 * 
 */
public abstract class Message {

    protected Element root;
    private Document  document;


    /**
     * Parse an XML document into a {@link Message} object.
     * 
     * @param xml
     *            A string containing the XML document to be parsed.
     * @return The resulting {@link Message} object.
     */
    public static Message parse(String xml) {

        try {
            // Build the document
            Builder parser = new Builder();
            Document doc = parser.build( xml, null );

            // Get the command type
            Element root = doc.getRootElement();
            Element commandElement = root.getFirstChildElement( "command" );
            BasicCommandSet command = BasicCommandSet.fromType( commandElement.getChild( 0 ).getValue() );

            // Instanciate that command and fill it up with the Message's data
            Message message = command.toClass().newInstance();
            message.root = root.getFirstChildElement( "data" );
            message.loadIn();

            return message;

        } catch (Exception e) {
            Logger.error( e, "Unspecified XML parsing error." );
        }

        return null;
    }

    protected abstract void writeOut();

    protected abstract void loadIn();

    protected Element addElement(String element, Object... contents) {

        return addElement( root, element, contents );
    }

    protected Element addElement(Element parent, String element, Object... contents) {

        Element simple = new Element( element );
        for (Object content : contents)
            simple.appendChild( content.toString() );

        parent.appendChild( simple );

        return simple;
    }

    protected Integer getInteger(String element) {

        return getInteger( root, element );
    }

    protected Integer getInteger(Element parent, String element) {

        try {
            return new Integer( getString( parent, element ) );
        } catch (NumberFormatException e) {
            Logger.error( e, "Message in parser is corrupt." );

            return 0;
        }
    }

    protected String getString(String element) {

        return getString( root, element );
    }

    protected String getString(Element parent, String element) {

        Element idEelement = parent.getFirstChildElement( element );
        return idEelement.getChild( 0 ).getValue();
    }

    /**
     * Convert this message into an XML document.
     * 
     * @return An XML document.
     */
    public String toXML() {

        if (document == null) {
            Element rootElement = new Element( "message" );
            Element commandElement = new Element( "command" );

            commandElement.appendChild( toString() );
            rootElement.appendChild( commandElement );

            // (Re)build XML tree from object's data
            root = new Element( "data" );
            writeOut();

            rootElement.appendChild( root );
            document = new Document( rootElement );
        }

        return document.toXML();

    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {

        return BasicCommandSet.fromClass( getClass() ).toString();
    }
}
