package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportType;

import java.util.List;

public interface AbstractIssueReportCreateView extends IsWidget {

    void setActivity(AbstractIssueReportCreateActivity activity);

    HasValue<En_ReportType> reportType();

    HasValue<En_ReportScheduledType> reportScheduledType();

    HasValue<String> name();

    void fillReportTypes(List<En_ReportType> options);

    void fillReportScheduledTypes(List<En_ReportScheduledType> options);

    HasWidgets getIssueFilterContainer();

    HasVisibility scheduledTypeContainerVisibility();

    HasVisibility checkImportanceHistoryContainerVisibility();

    HasValue<Boolean> checkImportanceHistory();
}
