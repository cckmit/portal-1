package ru.protei.portal.ui.issuereport.client.widget.issuefilter.model;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;
import java.util.Set;

public interface AbstractIssueFilter {
    void setModel(AbstractIssueFilterModel model);
    void reset();
    CaseQuery getValue();
    HasVisibility productsVisibility();
    HasVisibility managersVisibility();
    HasVisibility searchPrivateVisibility();
    HasValue<Set<EntityOption>> companies();
    void updateInitiators();
}
