package ru.protei.portal.ui.common.client.widget.report;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;

import java.util.Set;

public interface AbstractCaseCompletionTimeReportView extends IsWidget {

    void setActivity( AbstractIssueFilterParamActivity activity );

    AbstractIssueFilterParamActivity getActivity();

    HasValue<DateInterval> dateRange();

    HasValue<ProductShortView> products();

    HasValue<Set<En_CaseState>> states();

    void resetFilter();

}