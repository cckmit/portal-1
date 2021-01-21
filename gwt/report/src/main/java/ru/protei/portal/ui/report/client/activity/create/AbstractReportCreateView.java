package ru.protei.portal.ui.report.client.activity.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ReportAdditionalParamType;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

import java.util.List;
import java.util.Set;

public interface AbstractReportCreateView extends IsWidget {

    void setActivity(AbstractReportCreateActivity activity);

    HasValue<En_ReportType> reportType();

    HasValue<En_ReportScheduledType> reportScheduledType();

    HasValue<String> name();

    void fillReportTypes(List<En_ReportType> options);

    void fillReportScheduledTypes(List<En_ReportScheduledType> options);

    HasWidgets getFilterContainer();

    HasVisibility scheduledTypeContainerVisibility();

    HasVisibility additionalParamsVisibility();

    void setAdditionalParamsFilter(Selector.SelectorFilter<En_ReportAdditionalParamType> selectorFilter);

    HasValue<Set<En_ReportAdditionalParamType>> additionalParams();
}
