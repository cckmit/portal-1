package ru.protei.portal.ui.common.client.widget.wizard;

public interface WizardWidgetActivity {

    void onClose();

    void onDone();

    void onSaveAndContinue();

    default boolean canLeaveTab(String from, String to) { return true; }

    default boolean canGoBack(String from) { return true; }

    default boolean canGoNext(String from) { return true; }
}
