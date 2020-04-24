package ru.protei.portal.ui.common.client.activity.filter;

import ru.protei.portal.core.model.ent.CaseFilter;

import java.util.function.Consumer;
import java.util.function.Function;

public interface AbstractIssueFilterWidgetModel {
    void onRemoveClicked(Long id, Runnable afterRemove);
    void onOkSavingFilterClicked(String filterName, CaseFilter filledUserFilter,
                                 Consumer<CaseFilter> afterSave);
    void onUserFilterChanged(Long id, Consumer<CaseFilter> afterChange);
    void addAdditionalFilterValidate(Function<CaseFilter, Boolean> validate);
}
