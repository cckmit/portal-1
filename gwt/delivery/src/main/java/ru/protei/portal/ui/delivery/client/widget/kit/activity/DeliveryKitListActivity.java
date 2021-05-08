package ru.protei.portal.ui.delivery.client.widget.kit.activity;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class DeliveryKitListActivity implements AbstractDeliveryKitListActivity, Activity {
    @Override
    public void getLastSerialNumber(boolean isArmyProject, Consumer<String> success) {
        controller.getLastSerialNumber(isArmyProject, new FluentCallback<String>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    @Inject
    private DefaultErrorHandler defaultErrorHandler;
    @Inject
    private DeliveryControllerAsync controller;
}
