package ru.protei.portal.ui.common.client.activity.caselink.list;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.ui.common.client.widget.tab.pane.TabWidgetPane;

/**
 * Представление списка линков
 */
public interface AbstractCaseLinkListView extends IsWidget {

    void setActivity(AbstractCaseLinkListActivity activity);

    void showSelector(En_CaseType caseType, IsWidget target);

    HasVisibility getContainerVisibility();

    void setCountOfLinks(String tabName, String count);

    void addTabWidgetPane(TabWidgetPane tabWidgetPane);

    void tabVisibility(String tabName, boolean isVisible);

    void resetTabs();
}
