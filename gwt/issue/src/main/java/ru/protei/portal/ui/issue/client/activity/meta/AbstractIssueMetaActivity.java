package ru.protei.portal.ui.issue.client.activity.meta;

public interface AbstractIssueMetaActivity {
    void onInitiatorCompanyChanged();
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
    void onManagerChanged();
    default void onInitiatorChanged(){};
    default void onTimeElapsedChanged(){};
    void onDeadlineChanged();
    default void onWorkTriggerChanged(){};
}
