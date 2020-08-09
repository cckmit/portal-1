package ru.protei.portal.ui.common.client.columns;


import ru.brainworm.factory.widget.table.client.Selection;

/**
 * Провайдер выделения колонки в таблице
 */
public class ClickColumnProvider<T> {
    public void removeSelection (T row) {
        selectRowHandler.setRowSelected(row, false);
    }

    public void removeSelection() {
        removeSelection(this.selected);
        this.selected = null;
    }

    public T getSelected() {
        return selected;
    }

    void setSelectRowHandler(Selection.CanSelectRow.SelectRowHandler<T> selectRowHandler) {
        this.selectRowHandler = selectRowHandler;
    }

    void setSelectedValue(T selected) {
        this.selected = selected;
        selectRowHandler.setRowSelected( selected, true );
    }

    void changeSelection(T row) {
        selectRowHandler.setRowSelected( selected, false );
        selected = selected != row ? row : null;
        selectRowHandler.setRowSelected( selected, true );
    }

    private T selected;
    private Selection.CanSelectRow.SelectRowHandler< T > selectRowHandler;
}
