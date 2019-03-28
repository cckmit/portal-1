package ru.protei.portal.ui.common.client.view.report.timeresolution;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;

import java.util.Set;

public interface AbstractResolutionTimeReportView extends IsWidget {

    void setActivity( AbstractIssueFilterParamActivity activity );

    HasValue<DateInterval> dateRange();

    HasValue<Set<EntityOption>> companies();

    HasValue<Set<ProductShortView>> products();

    HasValue<Set<PersonShortView>> managers();

    HasValue<Set<En_ImportanceLevel>> importances();

    HasValue<Set<En_CaseState>> states();

    void resetFilter();

}
