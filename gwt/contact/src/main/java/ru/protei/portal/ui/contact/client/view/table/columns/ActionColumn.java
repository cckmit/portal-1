package ru.protei.portal.ui.contact.client.view.table.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import ru.brainworm.factory.widget.table.client.ColumnHeader;
import ru.brainworm.factory.widget.table.client.ColumnValue;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;

/**
 * Колонка действий над контактом.
 */
public abstract class ActionColumn< T > {

    public interface ActionHandler< T > extends AbstractColumnHandler< T > {
        void onActionClicked( T value );
    }

    public ActionColumn() {}

    // пустой заголовок
    public ColumnHeader header = new ColumnHeader() {
        @Override
        public void handleEvent( Event event ) {
        }

        @Override
        public void fillHeader( Element columnHeader ) {
        }
    };

    // значение ячейки
    public ColumnValue< T > values = new ColumnValue< T >() {

        @Override
        public void handleEvent( Event event, T value ) {

            if ( !"click".equalsIgnoreCase( event.getType() ) ) {
                return;
            }

            event.preventDefault();
            if ( handler != null ) {
                handler.onActionClicked( value );
            }
        }

        @Override
        public void fillValue( Element cell, T value ) {
            AnchorElement edit = DOM.createAnchor().cast();
            edit.setHref( "#" );
            edit.addClassName( "icon edit-icon" );
            cell.appendChild( edit );
        }
    };

    public void setHandler( ActionHandler< T > handler ) {
        this.handler = handler;
    }

    ActionHandler< T > handler;
}
