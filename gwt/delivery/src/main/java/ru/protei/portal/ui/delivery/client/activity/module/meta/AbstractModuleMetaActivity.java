package ru.protei.portal.ui.delivery.client.activity.module.meta;

public interface AbstractModuleMetaActivity {
    default void onStateChanged() {};

    default void onHwManagerChanged() {};

    default void onQcManagerChanged() {};

    void onBuildDateChanged();

    void onDepartureDateChanged();
}
