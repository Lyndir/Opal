package com.lyndir.lhunath.opal.system.dummy;

import com.google.common.base.Function;
import com.lyndir.lhunath.opal.system.util.StringUtils;


@SuppressWarnings( {
        "QuestionableName", "ConstantConditions", "LocalCanBeFinal", "ClassMayBeInterface", "InnerClassMayBeStatic", "UnusedParameters",
        "TypeParameterNamingConvention", "ClassNamingConvention", "UnusedDeclaration", "ProhibitedExceptionDeclared"
})
public class Spike {

    public static void main(final String... arguments)
            throws Exception {

        System.out.println( StringUtils.expand( "%{web.confidentialBaseURL}${publicPath:/}lisu-ws", "$", new Function<String, String>() {
            @Override
            public String apply(final String input) {

                return null;
            }
        } ));
    }
}
