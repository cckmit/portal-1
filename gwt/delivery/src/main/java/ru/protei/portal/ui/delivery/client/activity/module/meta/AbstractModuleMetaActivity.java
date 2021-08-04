package ru.protei.portal.ui.delivery.client.activity.module.meta;

public interface AbstractModuleMetaActivity {
    void onStateChange();

    void onHwManagerChange();

    void onQcManagerChange();

    void onDepartureDateChanged();

    String getValidationError();
}
