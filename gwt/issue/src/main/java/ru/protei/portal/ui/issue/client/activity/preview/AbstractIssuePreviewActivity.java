package ru.protei.portal.ui.issue.client.activity.preview;

import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueActivity;

/**
 * Абстракция активности превью обращения
 */
public interface AbstractIssuePreviewActivity extends AbstractIssueActivity {
    void onFullScreenPreviewClicked ();

    void onGoToIssuesClicked();
}
