package ru.protei.portal.ui.common.client.columns;


import ru.brainworm.factory.widget.table.client.Selection;

import java.util.function.Predicate;

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

    public void setChangeSelectionIfSelectedPredicate(Predicate<T> changeSelectionIfSelectedPredicate) {
        this.changeSelectionIfSelectedPredicate = changeSelectionIfSelectedPredicate;
    }

    void setSelectRowHandler(Selection.CanSelectRow.SelectRowHandler<T> selectRowHandler) {
        this.selectRowHandler = selectRowHandler;
    }

    void setSelectedValue(T selected) {
        this.selected = selected;
        selectRowHandler.setRowSelected( selected, true );
    }

    void changeSelection(T row) {
        if (!needChangeSelection(row, selected)) {
            return;
        }

        selectRowHandler.setRowSelected( selected, false );
        selected = selected != row ? row : null;
        selectRowHandler.setRowSelected( selected, true );
    }

    private boolean needChangeSelection(T row, T selected) {
        if (selected != row) {
            return true;
        }

        if (changeSelectionIfSelectedPredicate == null) {
            return true;
        }

        if (changeSelectionIfSelectedPredicate.test(row)) {
            return true;
        }

        return false;
    }

    private T selected;
    private Selection.CanSelectRow.SelectRowHandler< T > selectRowHandler;
    private Predicate<T> changeSelectionIfSelectedPredicate;
}
