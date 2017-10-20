package ru.protei.portal.ui.issue.client.activity.edit;

import ru.protei.portal.core.model.ent.Attachment;

/**
 * Created by bondarenko on 11.11.16.
 */
public interface AbstractIssueEditActivity {

    void onSaveClicked();
    void onCancelClicked();
    void removeAttachment(Attachment attachment);
    void onCompanyChanged();

}
