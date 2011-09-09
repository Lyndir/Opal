package com.lyndir.lhunath.opal.spike;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.util.Map;


/**
 * <i>09 08, 2011</i>
 *
 * @author lhunath
 */
public class GSonSpike {

    private static final Gson gson;

    static {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static void main(final String... arguments)
            throws Exception {

        gson.toJson( new Moo( ImmutableMap.<String, Object>builder().put( "moo", new Horse() ).build() ), System.out );
    }

    private static class Moo {

        @Expose
        Map<String, Object> mooMap;

        public Moo(final Map<String, Object> mooMap) {

            this.mooMap = mooMap;
        }
    }


    private static class Animal {

        boolean isHuman = false;
    }


    private static class Cow extends Animal {

        String says = "moo";
    }

    private static class Horse extends Animal {

        String says = "neigh";
    }
}
