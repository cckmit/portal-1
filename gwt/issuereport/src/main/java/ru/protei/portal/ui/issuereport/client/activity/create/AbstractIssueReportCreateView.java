package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;

import java.util.List;

public interface AbstractIssueReportCreateView extends IsWidget {

    void setActivity(AbstractIssueReportCreateActivity activity);

    HasValue<En_ReportType> reportType();

    HasValue<String> name();

    HasValue<CaseQuery> getIssueFilter();

    void resetFilter();

    void fillReportTypes( List<En_ReportType> options);

    HasVisibility productsVisibility();
    HasVisibility companiesVisibility();
    HasVisibility managersVisibility();
    HasVisibility commentAuthorsVisibility();
    HasVisibility tagsVisibility();
    HasVisibility searchPrivateVisibility();
    HasVisibility searchByCommentsVisibility();

}
