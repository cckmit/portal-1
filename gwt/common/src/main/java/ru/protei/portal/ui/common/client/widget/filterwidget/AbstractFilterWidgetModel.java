package ru.protei.portal.ui.common.client.widget.filterwidget;

import ru.protei.portal.core.model.view.filterwidget.Filter;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;
import ru.protei.portal.core.model.view.filterwidget.FilterShortView;

import java.util.function.Consumer;

public interface AbstractFilterWidgetModel<F extends Filter<FSV, ?>, FSV extends FilterShortView> {
    void onRemoveClicked(Long id, Runnable afterRemove);
    void onOkSavingFilterClicked(String filterName, F filledUserFilter,
                                 Consumer<F> afterSave);
    void onUserFilterChanged(Long id, Consumer<F> afterChange);
}
