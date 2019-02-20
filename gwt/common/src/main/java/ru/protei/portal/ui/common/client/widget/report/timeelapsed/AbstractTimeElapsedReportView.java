package ru.protei.portal.ui.common.client.widget.report.timeelapsed;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;

import java.util.Set;

public interface AbstractTimeElapsedReportView extends IsWidget {

    void setActivity( AbstractIssueFilterParamActivity activity );

    HasValue<DateInterval> dateRange();

    HasValue<Set<ProductShortView>> products();

    HasValue<Set<EntityOption>> companies();

    HasValue<Set<PersonShortView>> commentAuthors();

    HasVisibility productsVisibility();

    HasVisibility companiesVisibility();

    void resetFilter();

}

