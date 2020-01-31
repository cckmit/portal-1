package ru.protei.portal.ui.issuereport.client.widget.issuefilter.model;

import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.SelectorsParams;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.function.Consumer;

public interface AbstractIssueFilterModel {

    void onUserFilterChanged(Long id, Consumer<SelectorsParams> consumer);

    void onSaveFilterClicked(CaseFilter caseFilter, Consumer<CaseFilterShortView> consumer);

    void onRemoveFilterClicked(Long id);
}
