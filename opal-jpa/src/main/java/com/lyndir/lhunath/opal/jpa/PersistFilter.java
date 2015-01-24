package com.lyndir.lhunath.opal.jpa;

import com.google.inject.Singleton;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.IOException;
import javax.servlet.*;


/**
 * <h2>{@link PersistFilter}<br> <sub>[in short] (TODO).</sub></h2>
 * <p>
 * <p> <i>11 10, 2010</i> </p>
 *
 * @author lhunath
 */
@Singleton
public class PersistFilter implements Filter {

    static final Logger logger = Logger.get( PersistFilter.class );

    private final Persist persistence;

    private static Persist createPersistence() {

        try {
            return new Persist();
        } catch (final RuntimeException e) {
            throw logger.bug( e, "While initializing PersistFilter" );
        }
    }

    public PersistFilter() {

        this( createPersistence() );
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

            chain.doFilter( request, response );
        }
        catch (final Throwable t) {
            logger.err( t, "Uncaught throwable" );
            persistence.abort();
        }
        finally {
            persistence.complete( this );
        }
    }

    @Override
    public void destroy() {

        persistence.abort();
    }
}
