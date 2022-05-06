package ru.protei.portal.ui.report.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_ReportAdditionalParamType;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dict.En_TimeElapsedGroup;

import java.util.List;
import java.util.Set;

public interface AbstractReportEditView extends IsWidget {

    void setActivity(AbstractReportCreateEditActivity activity);

    HasValue<En_ReportType> reportType();

    HasEnabled reportTypeEnable();

    HasValue<En_ReportScheduledType> reportScheduledType();

    HasValue<String> name();

    void fillReportTypes(List<En_ReportType> options);

    void fillReportScheduledTypes(List<En_ReportScheduledType> options);

    HasWidgets getFilterContainer();

    HasVisibility scheduledTypeContainerVisibility();

    HasVisibility additionalParamsVisibility();

    HasValue<Set<En_ReportAdditionalParamType>> additionalParams();

    HasVisibility timeElapsedGroupVisibility();

    HasValue<Set<En_TimeElapsedGroup>> timeElapsedGroup();
}
