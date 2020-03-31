package ru.protei.portal.ui.issuereport.client.activity.create;

import ru.protei.portal.core.model.dict.En_CaseFilterType;

public interface AbstractIssueReportCreateActivity {
    void onReportTypeChanged(En_CaseFilterType filterType);
    void onSaveClicked();
    void onCancelClicked();
}
