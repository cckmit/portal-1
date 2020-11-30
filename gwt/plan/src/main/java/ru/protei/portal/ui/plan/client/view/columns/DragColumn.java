package ru.protei.portal.ui.plan.client.view.columns;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import ru.brainworm.factory.widget.table.client.CanDragRow;
import ru.brainworm.factory.widget.table.client.ColumnHeader;
import ru.brainworm.factory.widget.table.client.ColumnValue;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;
import ru.protei.portal.core.model.view.Identifiable;

/**
 * Колонка с возможностью перетаскивания строки таблицы
 */
public class DragColumn< T extends Identifiable> {

    public interface Handler< T > extends AbstractColumnHandler< T > {
        void onSwapItems ( T src, T dst );
    }

    public ColumnHeader header = new ColumnHeader() {
        @Override
        public void handleEvent( Event event ) {}

        @Override
        public void fillHeader( Element element ) {
            element.getStyle().setWidth( 30, Style.Unit.PX );
        }
    };

    public DragRow values = new DragRow();

    public class DragRow implements ColumnValue< T >, CanDragRow {

        @Override
        public void handleEvent(Event event, T value ) {
            if ( BrowserEvents.DRAGSTART.equals( event.getType() ) ) {
                src = value;
                event.getDataTransfer().setData("src/main/text/html", String.valueOf( src.getId() ) );
            }
            else if ( BrowserEvents.DRAGENTER.equals( event.getType() ) ) {
                dst = value;
                Element row = findParentRow( (Element)event.getEventTarget().cast() );
                if ( row != null ) {
                    row.addClassName( "over" );
                }
            }
            else if ( BrowserEvents.DRAGLEAVE.equals( event.getType() ) ) {
                Element row = findParentRow( (Element)event.getEventTarget().cast() );
                if ( row != null ) {
                    row.removeClassName( "over" );
                }
            }
            else if ( BrowserEvents.DRAGEND.equals( event.getType() ) ) {
                if ( handler != null ) {
                    handler.onSwapItems( src, dst );
                }
            }
        }

        Element findParentRow(Element src ) {
            while ( true ) {
                if ( src == null ) {
                    return null;
                }
                if ( "tr".equalsIgnoreCase( src.getTagName() ) ) {
                    return src;
                }
                src = src.getParentElement().cast();
            }
        }

        @Override
        public void fillValue(Element element, T value ) {
            element.setInnerHTML( "<i class='fas fa-arrows-alt-v' style=\"cursor: pointer; opacity: .5\"></i>" );
        }

        T src;
        T dst;
    }

    public void setHandler( Handler< T > handler ) {
        this.handler = handler;
    }

    Handler< T > handler;
}
