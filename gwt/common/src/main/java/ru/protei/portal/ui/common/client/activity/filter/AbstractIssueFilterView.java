package ru.protei.portal.ui.common.client.activity.filter;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterParamView;

/**
 * Абстракция вида фильтра обращений
 */
public interface AbstractIssueFilterView extends IsWidget {

    void setActivity(AbstractIssueFilterActivity activity);

    AbstractIssueFilterWidgetView getIssueFilterWidget();

    HasValue<String> filterName();

    HasVisibility removeFilterBtnVisibility();

    void resetFilter();

    HasEnabled createEnabled();

    void changeUserFilterValueName(CaseFilterShortView value);

    void addUserFilterDisplayOption(CaseFilterShortView value);

    void setFilterNameContainerErrorStyle(boolean hasError);

    void setUserFilterNameVisibility(boolean hasVisible);

    void setUserFilterControlsVisibility(boolean hasVisible);

    HasVisibility editBtnVisibility();

    IssueFilterParamView getIssueFilterParams();

    CaseQuery getValue();
}