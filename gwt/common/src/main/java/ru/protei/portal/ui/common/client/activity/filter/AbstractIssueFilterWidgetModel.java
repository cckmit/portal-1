package ru.protei.portal.ui.common.client.activity.filter;

import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.query.CaseQuery;

import java.util.function.Consumer;

public interface AbstractIssueFilterWidgetModel {
    void onRemoveClicked(Long id, Runnable afterRemove);
    void onOkSavingFilterClicked(String filterName, CaseFilterDto<CaseQuery> filledUserFilter,
                                 Consumer<CaseFilterDto<CaseQuery>> afterSave);
    void onUserFilterChanged(Long id, Consumer<CaseFilterDto<CaseQuery>> afterChange);
}
