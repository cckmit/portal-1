package ru.protei.portal.ui.delivery.client.activity.module.meta;

public interface AbstractModuleMetaActivity {
    void onStateChange();

    String getValidationError();
}
