package ru.protei.portal.ui.contact.client.view.table.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.brainworm.factory.widget.table.client.ColumnHeader;
import ru.brainworm.factory.widget.table.client.ColumnValue;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.brainworm.factory.widget.table.client.helper.ClickColumn;

/**
 * Колонка редактирования контакта.
 */
public abstract class EditClickColumn< T > extends ClickColumn < T > {

    public interface EditHandler< T > extends AbstractColumnHandler< T > {
        void onEditClicked(T value);
    }

    @Override
    protected void fillColumnHeader(Element element) {

    }

    @Override
    public void fillColumnValue( Element cell, T value ) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref( "#" );
        a.addClassName( "icon edit-icon" );
        cell.appendChild( a );

        DOM.sinkEvents( a, Event.ONCLICK );
        DOM.setEventListener( a, new EventListener() {
            @Override
            public void onBrowserEvent( Event event ) {

                if ( event.getTypeInt() != Event.ONCLICK ) {
                    return;
                }

                event.preventDefault();
                if ( editHandler != null ) {
                    editHandler.onEditClicked( value );
                }
            }
        });
    }

    public void setEditHandler( EditHandler< T > editHandler ) {
        this.editHandler = editHandler;
    }

    EditHandler< T > editHandler;
}
