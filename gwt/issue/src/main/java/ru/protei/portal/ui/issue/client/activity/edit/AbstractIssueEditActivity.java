package ru.protei.portal.ui.issue.client.activity.edit;

import ru.protei.portal.core.model.ent.Attachment;

public interface AbstractIssueEditActivity {

    void removeAttachment( Attachment attachment );

    void onCopyNumberClicked();

    void onEditNameAndDescriptionClicked( AbstractIssueEditView view );

    void onFullScreenPreviewClicked();

    void onBackClicked();

    void onCopyNumberAndName();
}

