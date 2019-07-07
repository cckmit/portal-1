package ru.protei.portal.ui.issuereport.client.widget.issuefilter.model;

import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.function.Consumer;

public interface AbstractIssueFilterActivity {

    void onUserFilterChanged(Long id, Consumer<CaseFilter> consumer);

    void onSaveFilterClicked(CaseFilter caseFilter, Consumer<CaseFilterShortView> consumer);

    void onRemoveFilterClicked(Long id);

    boolean validateQuery(En_CaseFilterType filterType, CaseQuery query);
}
