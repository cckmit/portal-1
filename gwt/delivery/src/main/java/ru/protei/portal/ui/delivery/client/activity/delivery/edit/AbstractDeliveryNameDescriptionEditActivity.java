package ru.protei.portal.ui.delivery.client.activity.delivery.edit;

import ru.brainworm.factory.generator.activity.client.activity.Activity;

public interface AbstractDeliveryNameDescriptionEditActivity extends Activity {
    void onNameDescriptionChanged();
    void saveIssueNameAndDescription();
}
