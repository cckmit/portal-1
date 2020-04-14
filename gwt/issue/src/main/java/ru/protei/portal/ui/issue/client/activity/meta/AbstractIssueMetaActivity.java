package ru.protei.portal.ui.issue.client.activity.meta;

public interface AbstractIssueMetaActivity {
    void onCompanyChanged();
    void onCreateContactClicked();
    default void onCaseMetaNotifiersChanged() {}
    default void onCaseMetaJiraChanged() {}

    void onImportanceChanged();
    void onPlatformChanged();
    void onProductChanged();
    default void onStateChange(){};
    default void onManagerChanged(){};
    default void onInitiatorChanged(){};
    default void onTimeElapsedChanged(){};
}
