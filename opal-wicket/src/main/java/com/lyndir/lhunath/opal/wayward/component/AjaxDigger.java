package com.lyndir.lhunath.opal.wayward.component;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import java.io.Serializable;
import java.util.*;
import javax.annotation.Nullable;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link AjaxDigger}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 20, 2010</i> </p>
 *
 * @author lhunath
 */
public class AjaxDigger<D extends Serializable> extends Panel {

    private final Stack<DiggerLevel> levels = new Stack<>();

    public AjaxDigger(final String id, final DiggerLevel initialLevel) {

        super( id );
        setOutputMarkupId( true );

        levels.push( initialLevel );
        add( new ListView<DiggerLevel>( "levels", levels ) {

            @Override
            protected void populateItem(final ListItem<DiggerLevel> levelListItem) {

                final DiggerLevel level = levelListItem.getModelObject();

                levelListItem.add( new Label( "heading", level.getHeading() ) );
                levelListItem.add( new ListView<DiggerItem>( "items", level.getItems() ) {

                    @Override
                    protected void populateItem(final ListItem<DiggerItem> itemListItem) {

                        final DiggerItem diggerItem = itemListItem.getModelObject();
                        itemListItem.add( new AjaxLink<Void>( "link" ) {

                            {
                                add( diggerItem.getSelectItem( "selectItem", level ) );
                            }

                            @Override
                            public boolean isVisible() {

                                return !ObjectUtils.isEqual( level.getActiveItem(), diggerItem );
                            }

                            @Override
                            public void onClick(final AjaxRequestTarget target) {

                                // Pop all items off the path from the clicked level up.
                                while (!levels.isEmpty() && !ObjectUtils.isEqual( levels.peek(), level ))
                                    levels.pop();

                                // Add the clicked item to the path.
                                level.setActiveItem( diggerItem );
                                DiggerLevel itemLevel = diggerItem.getLevel();
                                if (itemLevel != null)
                                    levels.push( itemLevel );

                                target.addComponent( AjaxDigger.this );
                            }
                        } );
                        itemListItem.add( diggerItem.getExpandedItem( "expandedItem", level )
                                // TODO: This should probably happen lazily:
                                                  .setVisible( ObjectUtils.isEqual( level.getActiveItem(), diggerItem ) ) );
                    }
                } );
            }
        } );
    }

    @SuppressWarnings("serial")
    public static class DiggerLevel implements Serializable {

        private final DiggerItem                parent;
        private final ImmutableList<DiggerItem> items;
        private final IModel<String>            heading;

        @Nullable
        private       DiggerItem                activeItem;

        public DiggerLevel(final IModel<String> heading, final DiggerItem parent, final Iterable<DiggerItem> items) {

            this.parent = parent;
            this.items = ImmutableList.copyOf( items );
            this.heading = heading;
        }

        public DiggerItem getParent() {

            return parent;
        }

        public ImmutableList<DiggerItem> getItems() {

            return items;
        }

        @Nullable
        public DiggerItem getActiveItem() {

            return activeItem;
        }

        public void setActiveItem(@Nullable final DiggerItem activeItem) {

            this.activeItem = activeItem;
        }

        public IModel<?> getHeading() {

            return heading;
        }
    }


    public interface DiggerItem extends Serializable {

        Component getSelectItem(String wicketId, DiggerLevel level);

        Component getExpandedItem(String wicketId, DiggerLevel level);

        DiggerLevel getLevel();
    }
}
