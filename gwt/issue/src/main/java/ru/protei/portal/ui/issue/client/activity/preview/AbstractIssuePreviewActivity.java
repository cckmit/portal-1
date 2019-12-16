package ru.protei.portal.ui.issue.client.activity.preview;

import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueDetailsView;
import ru.protei.portal.ui.issue.client.view.edit.AbstractIssueNameWidgetActivity;
import ru.protei.portal.ui.issue.client.view.preview.IssuePreviewView;

/**
 * Абстракция активности превью обращения
 */
public interface AbstractIssuePreviewActivity extends AbstractIssueNameWidgetActivity {
    void onFullScreenPreviewClicked ();
    void removeAttachment(Attachment attachment);
    void onGoToIssuesClicked();
    void onCopyNumberClicked();

    void onEditNameAndDescriptionClicked( AbstractIssueDetailsView issuePreviewView );

//    void onCopyNumberAndNameClicked();
}
