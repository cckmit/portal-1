package ru.protei.winter.web.common.client.activity.section;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.winter.web.common.client.common.DisplayStyle;

public interface AbstractSectionItemView extends IsWidget {

    void setActivity(AbstractSectionItemActivity var1);

    void setText(String var1);

    void setIcon(String var1);

    void setActive(boolean var1);

    void setCaret(Boolean var1);

    void setBadge(Integer var1);

    void setBadgeStyle(DisplayStyle var1);

    void setEnsureDebugId(String var1);

    void setSubSectionVisible(boolean var1);

    boolean isSubSectionVisible();

    HasWidgets getChildContainer();

    void toggleSubSection(Boolean var1);

    void setSectionTitle(String var1);

    void setHref(String var1);

    void addClickHandler();

    void setEnabled(boolean isEnabled);
}
