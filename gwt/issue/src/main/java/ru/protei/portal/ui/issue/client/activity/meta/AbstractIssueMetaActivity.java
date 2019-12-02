package ru.protei.portal.ui.issue.client.activity.meta;

import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;

public interface AbstractIssueMetaActivity {
    void onCaseMetaChanged(CaseObjectMeta caseMeta);
    void onCaseMetaNotifiersChanged(CaseObjectMetaNotifiers caseMetaNotifiers);
    void onCaseMetaJiraChanged(CaseObjectMetaJira caseMetaJira);
    void onCompanyChanged();
    void onCreateContactClicked();
}
