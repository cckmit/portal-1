package ru.protei.portal.ui.common.client.widget.filterwidget;

import ru.protei.portal.core.model.view.filterwidget.Filter;

import java.util.function.Consumer;

public interface AbstractFilterWidgetModel<F extends Filter<?, ?>> {
    void onRemoveClicked(Long id, Runnable afterRemove);
    void onOkSavingFilterClicked(String filterName, F filledUserFilter,
                                 Consumer<F> afterSave);
    void onUserFilterChanged(Long id, Consumer<F> afterChange);
}
