package com.lyndir.lhunath.opal.system.dummy;

/**
 * <i>06 25, 2011</i>
 *
 * @author lhunath
 */
@SuppressWarnings({
                          "QuestionableName", "ConstantConditions", "LocalCanBeFinal", "ClassMayBeInterface", "InnerClassMayBeStatic",
                          "UnusedParameters", "TypeParameterNamingConvention", "ClassNamingConvention", "UnusedDeclaration" })
public class Spike {

    public static void main(final String... args) {

    }

    class Engine implements Runnable {

        <J extends Job<A, I>, A extends Actor<I>, I extends Item> J nextJob() {

            return null;
        }

        <J extends Job<A, I>, A extends Actor<I>, I extends Item> void runJob(J job) {

        }

        @Override
        public void run() {

            //runJob( nextJob() );
        }
    }


    class Job<A extends Actor<I>, I extends Item> {

        private final A actor;
        private final I[] items;

        Job(A actor, I... items) {

            this.actor = actor;
            this.items = items;
        }

        public A getActor() {

            return actor;
        }

        public I[] getItems() {

            return items;
        }
    }


    class Actor<I extends Item> {

    }


    class Item {

    }
}
