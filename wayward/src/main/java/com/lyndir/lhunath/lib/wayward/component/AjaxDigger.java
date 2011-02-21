package com.lyndir.lhunath.lib.wayward.component;

import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Stack;
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

    private Stack<DiggerLevel> levels = new Stack<DiggerLevel>();

    public AjaxDigger(final String id, DiggerLevel initialLevel) {

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

    public static class DiggerLevel implements Serializable {

        private final DiggerItem       parent;
        private final List<DiggerItem> items;
        private       DiggerItem       activeItem;
        private       IModel<String>   heading;

        public DiggerLevel(final IModel<String> heading, final DiggerItem parent, final List<DiggerItem> items) {

            this.parent = parent;
            this.items = items;
            this.heading = heading;
        }

        public DiggerItem getParent() {

            return parent;
        }

        public List<DiggerItem> getItems() {

            return items;
        }

        public DiggerItem getActiveItem() {

            return activeItem;
        }

        public void setActiveItem(final DiggerItem activeItem) {

            this.activeItem = activeItem;
        }

        public IModel<?> getHeading() {
            return heading;
        }
    }


    public interface DiggerItem extends Serializable {

        Component getSelectItem(final String wicketId, final DiggerLevel level);

        Component getExpandedItem(final String wicketId, final DiggerLevel level);

        DiggerLevel getLevel();
    }
}
