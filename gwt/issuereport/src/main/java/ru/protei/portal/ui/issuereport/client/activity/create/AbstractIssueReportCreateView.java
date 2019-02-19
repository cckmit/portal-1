package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;

public interface AbstractIssueReportCreateView extends IsWidget {

    void setActivity(AbstractIssueReportCreateActivity activity);

    HasValue<En_ReportType> reportType();

    HasValue<String> name();

    HasValue<CaseFilterShortView> userFilter();

    void resetFilter();

    HasWidgets getReportContainer();

    void changeUserFilterValueName( CaseFilterShortView value );

    void addUserFilterDisplayOption( CaseFilterShortView value );

}
