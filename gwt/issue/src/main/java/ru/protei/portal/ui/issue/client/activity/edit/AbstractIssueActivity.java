package ru.protei.portal.ui.issue.client.activity.edit;

import ru.protei.portal.core.model.ent.Attachment;

public interface AbstractIssueActivity {
    void removeAttachment( Attachment attachment );

    void onCopyNumberClicked();

    void onCopyNumberAndName();
}
