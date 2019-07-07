package ru.protei.portal.ui.issuereport.client.widget.issuefilter.model;

import com.google.gwt.user.client.ui.HasVisibility;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.query.CaseQuery;

public interface AbstractIssueFilter {
    void setActivity(AbstractIssueFilterActivity activity);
    void resetFilter();
    CaseQuery getValue();
    void setValue(CaseQuery value);
    void setValue(CaseQuery value, boolean fireEvents);
    En_CaseFilterType getFilterType();
    HasVisibility productsVisibility();
    HasVisibility companiesVisibility();
    HasVisibility managersVisibility();
    HasVisibility searchPrivateVisibility();
}
