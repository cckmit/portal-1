package ru.protei.portal.ui.common.client.columns;


import ru.brainworm.factory.widget.table.client.Selection;

/**
 * Провайдер выделения колонки в таблице
 */
public class ClickColumnProvider<T> {

    public void setSelectRowHandler( Selection.CanSelectRow.SelectRowHandler< T > selectRowHandler ) {
        this.selectRowHandler = selectRowHandler;
    }

    public void setSelectedValue( T selected ) {
        this.selected = selected;
        selectRowHandler.setRowSelected( selected, true );
    }

    public void changeSelection( T row ) {
        selectRowHandler.setRowSelected( selected, false );
        selected = selected != row ? row : null;
        selectRowHandler.setRowSelected( selected, true );
    }

    public void removeSelection (T row) {
        selectRowHandler.setRowSelected(row, false);
    }

    public T getSelected() {
        return selected;
    }

    private T selected;
    private Selection.CanSelectRow.SelectRowHandler< T > selectRowHandler;
}
