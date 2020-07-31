package ru.protei.portal.ui.common.client.widget.filterwidget;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public interface FilterSelector<FSV> extends IsWidget, HasValue<FSV>, HasVisibility {
    void setEnsureDebugId(String debugId);
}
