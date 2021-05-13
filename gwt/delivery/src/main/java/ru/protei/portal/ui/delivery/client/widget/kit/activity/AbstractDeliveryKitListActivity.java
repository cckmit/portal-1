package ru.protei.portal.ui.delivery.client.widget.kit.activity;

import java.util.function.Consumer;

public interface AbstractDeliveryKitListActivity {
    void getLastSerialNumber(boolean isArmyProject, Consumer<String> success);
}
