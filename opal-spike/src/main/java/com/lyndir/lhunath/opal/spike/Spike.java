package com.lyndir.lhunath.opal.spike;

import com.lyndir.lhunath.opal.system.logging.Logger;
import java.util.regex.Pattern;


@SuppressWarnings({
                          "QuestionableName", "ConstantConditions", "LocalCanBeFinal", "ClassMayBeInterface", "InnerClassMayBeStatic",
                          "UnusedParameters", "TypeParameterNamingConvention", "ClassNamingConvention", "UnusedDeclaration",
                          "ProhibitedExceptionDeclared" })
public class Spike {

    static final Logger logger = Logger.get( Spike.class );

    private static final Pattern FRAGMENT_ELEMENT = Pattern.compile( "(([^/]+)(?:/|$))" );

    public static void main(final String... arguments)
            throws Exception {

    }
}
