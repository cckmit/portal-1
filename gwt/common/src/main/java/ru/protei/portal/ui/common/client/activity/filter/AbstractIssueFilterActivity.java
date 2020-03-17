package ru.protei.portal.ui.common.client.activity.filter;

import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.function.Consumer;

/**
 * Абстракция активности фильтра обращений
 */
public interface AbstractIssueFilterActivity {
    void onSaveFilterClicked();

    void onFilterRemoveClicked( Long id );

    void onOkSavingFilterClicked();

    void onCancelSavingFilterClicked();

    void onCreateFilterClicked();

    void onUserFilterChanged();

    void onSaveFilterClicked(CaseFilter caseFilter, Consumer<CaseFilterShortView> consumer);

    void onRemoveFilterClicked(Long id);
}