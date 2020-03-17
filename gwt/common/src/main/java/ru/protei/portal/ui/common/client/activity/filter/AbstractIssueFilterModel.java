package ru.protei.portal.ui.common.client.activity.filter;

import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.function.Consumer;

public interface AbstractIssueFilterModel {

    void onUserFilterChanged(Long id, Consumer<CaseFilter> consumer);

    void onSaveFilterClicked(CaseFilter caseFilter, Consumer<CaseFilterShortView> consumer);

    void onRemoveFilterClicked(Long id);
}
