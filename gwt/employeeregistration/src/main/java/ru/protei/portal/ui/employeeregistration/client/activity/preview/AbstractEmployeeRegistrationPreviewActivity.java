package ru.protei.portal.ui.employeeregistration.client.activity.preview;

import ru.protei.portal.core.model.dict.En_MultiTabWidgetTabs;

import java.util.List;

public interface AbstractEmployeeRegistrationPreviewActivity {

    void onFullScreenPreviewClicked();

    void onBackButtonClicked();

    void selectedTabsChanged(List<En_MultiTabWidgetTabs> selectedTabs);
}
