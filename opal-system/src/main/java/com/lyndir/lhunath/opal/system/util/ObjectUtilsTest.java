package com.lyndir.lhunath.opal.system.util;

import com.google.common.base.Function;
import com.lyndir.lhunath.opal.system.collection.Holder;


/**
 * <i>06 21, 2011</i>
 *
 * @author lhunath
 */
public class ObjectUtilsTest {

    public static void main(String... args) {

        final Holder<String> foo = new Holder<String>( "foo" );
        System.out.println( "0s: " + foo.get() );
        new Function() {

            @Override
            public Object apply(final Object input) {

                try {
                    System.out.println( "1s: " + foo.get() );
                    return new Function() {
                        @Override
                        public Object apply(final Object input) {

                            try {
                                System.out.println( "2s: " + foo.get() );
                                foo.set( "bar" );
                                System.out.println( "2m: " + foo.get() );
                                return null;
                            }
                            finally {
                                System.out.println( "2e: " + foo.get() );
                            }
                        }
                    }.apply( input );
                }
                finally {
                    System.out.println( "1e: " + foo.get() );
                }
            }
        }.apply( null );
        System.out.println( "0e: " + foo.get() );
    }
}
