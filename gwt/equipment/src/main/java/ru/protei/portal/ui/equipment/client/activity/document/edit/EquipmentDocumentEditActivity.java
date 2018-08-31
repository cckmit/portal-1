package ru.protei.portal.ui.equipment.client.activity.document.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;

public abstract class EquipmentDocumentEditActivity implements Activity, AbstractEquipmentDocumentEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);

    }


    @Inject
    Lang lang;
    @Inject
    AbstractEquipmentDocumentEditView view;
    @Inject
    DocumentControllerAsync documentController;
}
