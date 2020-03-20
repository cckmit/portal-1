package ru.protei.portal.ui.issue.client.activity.meta;

public interface AbstractIssueMetaActivity {
    void onCompanyChanged();
    void onCreateContactClicked();
    default void onCaseMetaNotifiersChanged() {}
    default void onCaseMetaJiraChanged() {}

    default void onStateChange(){};
    default void onImportanceChanged(){};
    default void onProductChanged(){};
    default void onManagerChanged(){};
    default void onInitiatorChanged(){};
    default void onPlatformChanged(){};
    default void onTimeElapsedChanged(){};
    default void onJiraInfoClicked(){};
}
