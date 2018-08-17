package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import ru.brainworm.factory.widget.table.client.ColumnHeader;
import ru.brainworm.factory.widget.table.client.ColumnValue;
import ru.brainworm.factory.widget.table.client.Selection;
import ru.brainworm.factory.widget.table.client.helper.AbstractColumnHandler;

/**
 * Стандартная колонка с текстовым заполнением c возможностью выбора строки по клику
 */
public abstract class ClickColumn<T> {

    public interface Handler<T> extends AbstractColumnHandler<T> {
        void onItemClicked ( T value );
    }

    public ColumnHeader header = new ColumnHeader() {
        @Override
        public void handleEvent( Event event ) {}

        @Override
        public void fillHeader( Element columnHeader ) {
            fillColumnHeader( columnHeader );
        }
    };

    public SelectionRow values = new SelectionRow();

    public class SelectionRow implements ColumnValue<T>, Selection.CanSelectRow<T> {

        @Override
        public void setSelectRowHandler( SelectRowHandler< T > selectRowHandler ) {
            this.selectRowHandler = selectRowHandler;
        }

        @Override
        public void handleEvent( Event event, T value ) {
            if ( !"click".equalsIgnoreCase( event.getType() ) ) {
                return;
            }

            com.google.gwt.dom.client.Element target = event.getEventTarget().cast();

            // сделано посредством обработки клика в базовой колонке,
            // чтобы не плодить хэндлеры на конкретных элементах в реализации
            if ( stopPropogationElementClassName != null
                    && target.getClassName().contains(stopPropogationElementClassName)) {
                event.stopPropagation();
                return;
            }

            if ( "a".equalsIgnoreCase( target.getNodeName() ) ) {
                if (actionClickHandler != null) {
                    event.preventDefault();
                    columnProvider.setSelectedValue( value );
                    actionClickHandler.onItemClicked(value);
                }
                return;
            }

            event.preventDefault();
            if ( columnProvider == null ) {
                return;
            }

           columnProvider.changeSelection( value );
            if ( columnClickHandler != null ) {
                columnClickHandler.onItemClicked( columnProvider.getSelected() );
            }
        }

        @Override
        public void fillValue( Element cell, T value ) {
            cell.getStyle().setCursor( Style.Cursor.POINTER );
            fillColumnValue( cell, value );
            if(columnProvider!=null){
                if(columnProvider.getSelected()!= null && columnProvider.getSelected().equals(value)){
                    columnProvider.setSelectedValue(value);
                }
            }
        }

        public SelectRowHandler< T > getSelectRowHandler() {
            return selectRowHandler;
        }

        private SelectRowHandler< T > selectRowHandler;
    }

    protected abstract void fillColumnHeader(Element columnHeader);

    public abstract void fillColumnValue( Element cell, T value );

    public void setHandler( Handler<T> handler ) {
        this.columnClickHandler = handler;
    }

    public void setActionHandler( Handler<T> handler ) {
        this.actionClickHandler = handler;
    }

    public void setColumnProvider( ClickColumnProvider< T > columnProvider ) {
        this.columnProvider = columnProvider;
        columnProvider.setSelectRowHandler( values.getSelectRowHandler() );
    }

    public void setStopPropogationElementClassName( String className ) {
        this.stopPropogationElementClassName = className;
    }

    Handler<T> columnClickHandler;
    Handler<T> actionClickHandler;
    ClickColumnProvider<T> columnProvider;
    String stopPropogationElementClassName = null;
}
