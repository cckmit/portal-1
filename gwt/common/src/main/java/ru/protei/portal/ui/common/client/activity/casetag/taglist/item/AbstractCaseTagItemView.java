package ru.protei.portal.ui.common.client.activity.casetag.taglist.item;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление тега
 */
public interface AbstractCaseTagItemView extends IsWidget {

    void setActivity(AbstractCaseTagItemActivity activity);

    void setEnabled(boolean enabled);

    void setName(String name);

    void setColor(String color);
}
