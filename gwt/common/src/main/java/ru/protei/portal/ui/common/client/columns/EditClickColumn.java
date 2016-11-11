package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка редактирования контакта.
 */
public abstract class EditClickColumn< T > extends ru.protei.portal.ui.common.client.columns.ClickColumn< T > {

    public interface EditHandler< T > extends AbstractColumnHandler< T > {
        void onEditClicked(T value);
    }

    @Inject
    public EditClickColumn ( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element element) {

    }

    @Override
    public void fillColumnValue( Element cell, T value ) {
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref( "#" );
        a.addClassName( "icon edit-icon" );
        a.setTitle( lang.edit() );
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
            if ( editHandler != null ) {
                editHandler.onEditClicked( value );
            }
        });
    }

    public void setEditHandler( EditHandler< T > editHandler ) {
        this.editHandler = editHandler;
    }

    Lang lang;

    EditHandler< T > editHandler;
}
