package ru.protei.portal.ui.common.client.activity.casehistory.item.caseinfo;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractCaseInfoHistoryItemView extends IsWidget {

    void setDescription(String issueDescription);

    HasVisibility loadingViewVisibility();
}
