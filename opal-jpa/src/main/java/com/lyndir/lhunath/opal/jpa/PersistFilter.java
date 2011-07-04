package com.lyndir.lhunath.opal.jpa;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.servlet.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <h2>{@link PersistFilter}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>11 10, 2010</i> </p>
 *
 * @author lhunath
 */
public class PersistFilter implements Filter {

    static final Logger logger = LoggerFactory.getLogger( PersistFilter.class );

    private static final ThreadLocal<Persist> persistences = new ThreadLocal<Persist>();
    private final Persist persistence;

    public PersistFilter() {

        this( new Persist() );
    }

    public PersistFilter(final Persist persistence) {

        this.persistence = persistence;
    }

    @Override
    public void init(final FilterConfig filterConfig)
            throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        try {
            persistence.begin( this );
            persistences.set( persistence );

            chain.doFilter( request, response );
        }
        catch (Throwable t) {
            logger.error( "Uncaught throwable", t );
            persistence.abort();
        }
        finally {
            persistences.remove();
            persistence.complete( this );
        }
    }

    @NotNull
    public static Persist persist() {

        return checkNotNull( persistences.get(), "No persistence active." );
    }

    @NotNull
    public static EntityManager entityManager() {

        return persist().entityManager();
    }

    @Override
    public void destroy() {

        persistence.abort();
    }
}
