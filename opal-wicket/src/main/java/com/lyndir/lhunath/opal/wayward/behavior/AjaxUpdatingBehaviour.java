package com.lyndir.lhunath.opal.wayward.behavior;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.js.JSUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.*;


/**
 * <i>08 27, 2011</i>
 *
 * @author lhunath
 */
public class AjaxUpdatingBehaviour implements IBehavior {

    static final Logger logger = Logger.get( AjaxUpdatingBehaviour.class );

    private Component boundComponent;

    @Override
    public void beforeRender(final Component component) {

    }

    @Override
    public void afterRender(final Component component) {

    }

    @Override
    public void bind(final Component component) {

        boundComponent = component;
        component.setOutputMarkupId( true );

        if (component instanceof RadioChoice || component instanceof CheckBoxMultipleChoice || component instanceof RadioGroup
            || component instanceof CheckGroup)
            component.add( new AjaxFormChoiceComponentUpdatingBehavior() {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {

                    AjaxUpdatingBehaviour.this.onUpdate( target );
                }
            } );
        else if (component instanceof FormComponent)
            component.add( new OnChangeAjaxBehavior() {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {

                    AjaxUpdatingBehaviour.this.onUpdate( target );
                }
            } );
        else
            throw logger.bug( "This behaviour is only supported on form components.  It was added to: %s", component );
    }

    @Override
    public void detach(final Component component) {

    }

    @Override
    public void exception(final Component component, final RuntimeException exception) {

    }

    @Override
    public boolean getStatelessHint(final Component component) {

        return true;
    }

    @Override
    public boolean isEnabled(final Component component) {

        return false;
    }

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {

    }

    @Override
    public boolean isTemporary() {

        return true;
    }

    protected void onUpdate(final AjaxRequestTarget target) {

        setCSSClass( target, "uptodate" );
    }

    private void setCSSClass(final AjaxRequestTarget target, final String cssClass) {

        target.appendJavascript( JSUtils.format(
                "document.getElementById(%s).className = (' ' + document.getElementById(%s).className + ' ').replace(' uptodate ', ' ').replace(' outdated ', ' ').replace(' *$', ' ' + %s).replace('^ *', '')",
                boundComponent.getMarkupId(), boundComponent.getMarkupId(), cssClass ) );
    }
}
