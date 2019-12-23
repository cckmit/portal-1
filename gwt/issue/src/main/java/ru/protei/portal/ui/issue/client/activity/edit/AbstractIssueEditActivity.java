package ru.protei.portal.ui.issue.client.activity.edit;

import ru.protei.portal.core.model.ent.Attachment;

public interface AbstractIssueEditActivity {

    void removeAttachment( Attachment attachment );

    void onNameAndDescriptionEditClicked();

    void onCopyNumberClicked();

    void onCopyNumberAndName();

    void onOpenEditViewClicked();

    void onBackClicked();
}
