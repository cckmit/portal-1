package ru.protei.portal.ui.common.client.activity.info;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface AbstractJiraInfoView extends IsWidget {
    void setActivity(AbstractJiraInfoActivity activity);

    void setData(List<JiraInfoActivity.JiraStatusInfo> infos);

    void setImage(String imageUrl);
}
