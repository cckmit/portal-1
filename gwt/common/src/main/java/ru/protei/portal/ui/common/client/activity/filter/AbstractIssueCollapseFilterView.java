package ru.protei.portal.ui.common.client.activity.filter;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида фильтра обращений
 */
public interface AbstractIssueCollapseFilterView extends IsWidget {

    void setActivity(AbstractIssueFilterCollapseActivity activity);

    HasWidgets getContainer();
}