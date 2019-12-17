package ru.protei.portal.ui.issue.client.activity.preview;

import ru.protei.portal.core.model.ent.Attachment;

/**
 * Абстракция активности превью обращения
 */
public interface AbstractIssuePreviewActivity  {
    void onFullScreenPreviewClicked ();
    void removeAttachment(Attachment attachment);
    void onGoToIssuesClicked();
    void onCopyNumberClicked();

    void onCopyNumberAndName();
}
