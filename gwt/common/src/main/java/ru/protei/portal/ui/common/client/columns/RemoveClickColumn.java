package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.ent.Removable;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class RemoveClickColumn< T > extends ClickColumn< T > {

    public interface RemoveHandler< T > extends AbstractColumnHandler< T > {
        void onRemoveClicked( T value );
    }

    @Inject
    public RemoveClickColumn ( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element element) {
        element.addClassName( "remove" );
    }

    @Override
    public void fillColumnValue( Element cell, T value ) {

        if ( ( ( Removable )value ).isAllowedRemove() ) {

            AnchorElement a = DOM.createAnchor().cast();
            a.setHref( "#" );
            a.addClassName( "fa-1-9x fa fa-trash-o" );
            a.setTitle( lang.remove() );
            cell.appendChild( a );

            DOM.sinkEvents( a, Event.ONCLICK );
            DOM.setEventListener( a, (event) -> {
                if ( event.getTypeInt() != Event.ONCLICK ) {
                    return;
                }

                com.google.gwt.dom.client.Element target = event.getEventTarget().cast();
                if ( !"a".equalsIgnoreCase( target.getNodeName() ) ) {
                    return;
                }

                event.preventDefault();
                if ( removeHandler != null ) {
                    removeHandler.onRemoveClicked( value );
                }
            });
        }
    }

    public void setRemoveHandler( RemoveHandler< T > removeHandler ) {
        this.removeHandler = removeHandler;
    }

    Lang lang;

    RemoveHandler< T > removeHandler;
}
