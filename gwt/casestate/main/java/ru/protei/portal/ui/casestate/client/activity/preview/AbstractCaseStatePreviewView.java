package ru.protei.portal.ui.casestate.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractCaseStatePreviewView extends IsWidget {
    void setActivity(AbstractCaseStatePreviewActivity activity);

    void setHeader(String header);

    void setName(String stateName);

    void setDescription(String description);
}
