package com.lyndir.lhunath.opal.wayward.behavior;

import com.lyndir.lhunath.opal.system.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;


/**
 * <i>08 27, 2011</i>
 *
 * @author lhunath
 */
public class AjaxSubmitBehavior extends AjaxFormComponentUpdatingBehavior {

    static final Logger logger = Logger.get( AjaxSubmitBehavior.class );

    public AjaxSubmitBehavior() {

        super( "onKeyUp" );
    }

    @Override
    protected void onUpdate(final AjaxRequestTarget target) {

    }

    @Override
    protected CharSequence getPreconditionScript() {

        return "if (event.keyCode != 13) return false; " + super.getPreconditionScript();
    }
}
