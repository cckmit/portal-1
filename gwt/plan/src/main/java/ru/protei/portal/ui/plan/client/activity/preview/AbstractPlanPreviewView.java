package ru.protei.portal.ui.plan.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractPlanPreviewView extends IsWidget {
    void setActivity(AbstractPlanPreviewActivity activity);

    void setHeader(String value);

    void setName(String value);

    void setCreatedBy(String value);

    void setPeriod(String value);

    void setIssues(String value);

    void showFullScreen(boolean isFullScreen);
}
