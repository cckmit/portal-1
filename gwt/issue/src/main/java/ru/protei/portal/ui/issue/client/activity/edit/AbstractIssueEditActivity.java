package ru.protei.portal.ui.issue.client.activity.edit;

public interface AbstractIssueEditActivity extends AbstractIssueActivity {

    void onNameAndDescriptionEditClicked( AbstractIssueEditView view );

    void onFullScreenPreviewClicked();

    void onBackClicked();
}
