package com.lyndir.lhunath.opal.system.dummy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings({
                          "QuestionableName", "ConstantConditions", "LocalCanBeFinal", "ClassMayBeInterface", "InnerClassMayBeStatic",
                          "UnusedParameters", "TypeParameterNamingConvention", "ClassNamingConvention", "UnusedDeclaration",
                          "ProhibitedExceptionDeclared" })
public class Spike {

    private static final Pattern FRAGMENT_ELEMENT = Pattern.compile( "(([^/]+)(?:/|$))" );

    public static void main(final String... arguments)
            throws Exception {

        Matcher matcher = FRAGMENT_ELEMENT.matcher( "abc/def/ghi" );
        while (matcher.find()) {
            System.out.println( "<<<" );
            System.out.println( matcher.group( 0 ) );
            System.out.println( matcher.group( 1 ) );
            System.out.println( matcher.group( 2 ) );
            System.out.println( ">>>" );
        }
    }
}
