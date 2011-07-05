package com.lyndir.lhunath.opal.jpa;

import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.IOException;
import javax.servlet.*;


/**
 * <h2>{@link PersistFilter}<br> <sub>[in short] (TODO).</sub></h2>
 * <p/>
 * <p> <i>11 10, 2010</i> </p>
 *
 * @author lhunath
 */
public class PersistFilter implements Filter {

    static final Logger logger = Logger.get( PersistFilter.class );

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

            chain.doFilter( request, response );
        }
        catch (Throwable t) {
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
