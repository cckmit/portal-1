package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilter;

import java.util.List;

public interface AbstractIssueReportCreateView extends IsWidget {

    void setActivity(AbstractIssueReportCreateActivity activity);

    HasValue<En_ReportType> reportType();

    HasValue<String> name();

    AbstractIssueFilter getIssueFilter();

    void reset();

    void fillReportTypes(List<En_ReportType> options);
}
