package ru.protei.portal.ui.common.client.activity.caselink.list;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;

/**
 * Представление списка линков
 */
public interface AbstractCaseLinkListView extends IsWidget {

    void setActivity(AbstractCaseLinkListActivity activity);

    void setLinksContainerVisible(boolean isVisible);

    HasWidgets getLinksContainer();

    void setHeader(String value);

    HasEnabled addButtonEnabled();
}
