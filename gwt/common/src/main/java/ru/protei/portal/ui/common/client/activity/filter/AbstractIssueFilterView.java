package ru.protei.portal.ui.common.client.activity.filter;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;

import java.util.function.Function;

/**
 * Абстракция вида фильтра обращений
 */
public interface AbstractIssueFilterView extends IsWidget {
    void resetFilter();

    HasEnabled createEnabled();

    AbstractIssueFilterWidgetView getIssueFilterParams();

    void presetFilterType();

    void updateFilterType(En_CaseFilterType filterType);

    void showUserFilterControls();

    void addAdditionalFilterValidate(Function<CaseFilter, Boolean> validate);

    CaseQuery getFilterFieldsByFilterType();
}