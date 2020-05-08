package ru.protei.portal.ui.common.client.activity.casetag.taglist;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление списка тегов
 */
public interface AbstractCaseTagListView extends IsWidget {

    void setActivity(AbstractCaseTagListActivity activity);

    HasWidgets getTagsContainer();

    HasVisibility getTagsContainerVisibility();

    boolean isAttached();
}
