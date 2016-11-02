package ru.protei.portal.ui.contact.client.view.table.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.ColumnHeader;
import ru.brainworm.factory.widget.table.client.ColumnValue;
import ru.brainworm.factory.widget.table.client.Selection;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка редактирования контакта.
 */
public abstract class EditActionClickColumn< T > {

    public interface Handler<T> extends AbstractColumnHandler<T> {
        void onItemClicked( T value );
    }

    public interface EditHandler< T > extends AbstractColumnHandler< T > {
        void onEditClicked(T value);
    }

    @Inject
    public EditActionClickColumn ( Lang lang ) {
        this.lang = lang;
    }

    public ColumnHeader header = new ColumnHeader() {
        @Override
        public void handleEvent( Event event ) {}

        @Override
        public void fillHeader( Element columnHeader ) {
            fillColumnHeader( columnHeader );
        }
    };

    public ColumnValue<T> values = new SelectionRow();

    public class SelectionRow implements ColumnValue<T>, Selection.CanSelectRow<T> {
        @Override
        public void setSelectRowHandler( SelectRowHandler< T > selectRowHandler ) {
            this.selectRowHandler = selectRowHandler;
        }

        @Override
        public void handleEvent( Event event, T value ) {
            if ( event.getTypeInt() != Event.ONCLICK ) {
                return;
            }

            com.google.gwt.dom.client.Element target = event.getEventTarget().cast();
            if ( "a".equalsIgnoreCase( target.getNodeName() ) ) {
                return;
            }

            event.preventDefault();
            if ( selectRowHandler == null ) {
                return;
            }
            selectRowHandler.setRowSelected( selectedRow, false );
            if ( value != selectedRow ) {
                selectRowHandler.setRowSelected( value, true );
                selectedRow = value;
            } else {
                selectedRow = null;
            }

            if ( handler != null ) {
                handler.onItemClicked( selectedRow );
            }
        }

        @Override
        public void fillValue( Element cell, T value ) {
            cell.getStyle().setCursor( Style.Cursor.POINTER );
            fillColumnValue( cell, value );
            if ( selectRowHandler != null && selectedRow != null && selectedRow.equals( value ) ) {
                selectRowHandler.setRowSelected( value, true );
            }
        }

        protected SelectRowHandler<T> selectRowHandler;
    }

    protected void fillColumnHeader(Element columnHeader) {

    };

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
            if ( !"a".equalsIgnoreCase(target.getNodeName() ) ) {
                return;
            }

            event.preventDefault();
            if ( editHandler != null ) {
                editHandler.onEditClicked( value );
            }
        });
    };

    public void setHandler( Handler<T> handler ) {
        this.handler = handler;
    }

    public void setEditHandler( EditHandler< T > editHandler ) {
        this.editHandler = editHandler;
    }

    public void setSelectedRow( T value ) {
        this.selectedRow = value;
    }

    Lang lang;
    T selectedRow;
    Handler<T> handler;
    EditHandler< T > editHandler;

}