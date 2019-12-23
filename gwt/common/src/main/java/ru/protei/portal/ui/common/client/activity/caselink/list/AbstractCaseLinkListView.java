package ru.protei.portal.ui.common.client.activity.caselink.list;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление списка линков
 */
public interface AbstractCaseLinkListView extends IsWidget {

    void setActivity(AbstractCaseLinkListActivity activity);

    void showSelector(IsWidget anchor);

    void setLinksContainerVisible(boolean isVisible);

    HasWidgets getLinksContainer();

    HasVisibility getContainerVisibility();

    void setHeader(String value);
}
