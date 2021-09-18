package ru.protei.portal.ui.delivery.client.activity.delivery.module.edit;

import ru.brainworm.factory.generator.activity.client.activity.Activity;

public interface AbstractModuleNameDescriptionEditActivity extends Activity {
    void onNameDescriptionChanged();
    void saveModuleNameAndDescription();
}
