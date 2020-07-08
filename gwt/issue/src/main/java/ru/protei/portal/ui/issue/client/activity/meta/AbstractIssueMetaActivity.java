package ru.protei.portal.ui.issue.client.activity.meta;

public interface AbstractIssueMetaActivity {
    void onCompanyChanged();
    void onManagerCompanyChanged();
    void onCreateContactClicked();
    default void onCaseMetaNotifiersChanged() {}
    default void onCaseMetaJiraChanged() {}

    void onImportanceChanged();
    void onPlatformChanged();
    void onProductChanged();
    void onPauseDateChanged();
    void onStateChange();
    void onPlansChanged();
    default void onManagerChanged(){};
    default void onInitiatorChanged(){};
    default void onTimeElapsedChanged(){};
}
