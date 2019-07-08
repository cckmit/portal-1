package ru.protei.portal.ui.issuereport.client.widget.issuefilter.model;

import com.google.gwt.user.client.ui.HasVisibility;
import ru.protei.portal.core.model.query.CaseQuery;

public interface AbstractIssueFilter {
    void setModel( AbstractIssueFilterModel activity);
    void reset();
    CaseQuery getValue();
    HasVisibility productsVisibility();
    HasVisibility companiesVisibility();
    HasVisibility managersVisibility();
    HasVisibility searchPrivateVisibility();
}
