package ru.protei.portal.ui.company.client.view.edit.columns;


import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.ColumnHeader;
import ru.brainworm.factory.widget.table.client.ColumnValue;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Created by turik on 09.11.16.
 */
public class EditColumn< T > {

    public interface EditHandler< T > {
        void onEditClicked ( T value );
    }

    @Inject
    public EditColumn ( Lang lang ) {
        this.lang = lang;
    }

    // пустой заголовок
    public ColumnHeader header = new ColumnHeader() {
        @Override
        public void handleEvent( Event event ) {

        }

        @Override
        public void fillHeader( Element element ) {

        }
    };

    // значение ячейки
    public ColumnValue< T > values = new ColumnValue< T >() {
        @Override
        public void handleEvent( Event event, T t ) {
            if ( !"click".equalsIgnoreCase( event.getType() ) ) {
                return;
            }

            com.google.gwt.dom.client.Element target = event.getEventTarget().cast();
            if ( !"a".equalsIgnoreCase(target.getNodeName() ) ) {
                return;
            }

            event.preventDefault();
            if ( handler != null ) {
                handler.onEditClicked( t );
            }
        }

        @Override
        public void fillValue( Element element, T t ) {
            AnchorElement a = DOM.createAnchor().cast();
            a.setHref( "#" );
            a.addClassName( "icon edit-icon" );
            a.setTitle( lang.edit() );
            element.appendChild( a );
        }
    };

    public void setHandler( EditHandler< T > handler ) {
        this.handler = handler;
    }

    Lang lang;

    EditHandler< T > handler;
}
