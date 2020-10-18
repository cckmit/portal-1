package ru.protei.portal.ui.common.client.widget.tab.base;

import com.google.gwt.user.client.ui.HasVisibility;

public interface AbstractNavItem extends HasVisibility {
    void setActive();
    void setInActive();
    void setTabNameDebugId(String debugId);
}
