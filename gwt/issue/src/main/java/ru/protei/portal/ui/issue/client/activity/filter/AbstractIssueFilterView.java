package ru.protei.portal.ui.issue.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;

/**
 * Абстракция вида фильтра обращений
 */
public interface AbstractIssueFilterView extends IsWidget {

    void setActivity(AbstractIssueFilterActivity activity);

    AbstractIssueFilterWidgetView getIssueFilterWidget();

    void resetFilter();

    void changeUserFilterValueName(CaseFilterShortView value);

    void addUserFilterDisplayOption(CaseFilterShortView value);
}