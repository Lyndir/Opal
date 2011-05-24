package com.lyndir.lhunath.opal.wayward.renderer;

import java.io.Serializable;
import org.apache.wicket.markup.html.form.IChoiceRenderer;


/**
 * <h2>{@link ChoiceRenderer}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>03 23, 2011</i> </p>
 *
 * @author lhunath
 */
public class ChoiceRenderer<T extends ChoiceRenderer.Choice> implements IChoiceRenderer<T> {

    @Override
    public Object getDisplayValue(final T object) {

        return object.getDisplayValue();
    }

    @Override
    public String getIdValue(final T object, final int index) {

        return object.getIdValue();
    }

    public interface Choice {

        Serializable getDisplayValue();

        String getIdValue();
    }
}
