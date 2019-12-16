package ru.protei.portal.ui.issue.client.activity.meta;

import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;

public interface AbstractIssueMetaActivity {
    void onCompanyChanged();
    void onCreateContactClicked();
    default void onCaseMetaChanged(CaseObjectMeta caseMeta) {}
    default void onCaseMetaNotifiersChanged(CaseObjectMetaNotifiers caseMetaNotifiers) {}
    default void onCaseMetaJiraChanged(CaseObjectMetaJira caseMetaJira) {}
}
