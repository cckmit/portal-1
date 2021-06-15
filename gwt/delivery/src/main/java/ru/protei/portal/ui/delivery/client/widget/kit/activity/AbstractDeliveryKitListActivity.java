package ru.protei.portal.ui.delivery.client.widget.kit.activity;

import ru.protei.portal.core.model.ent.CaseState;

import java.util.function.Consumer;

public interface AbstractDeliveryKitListActivity {
    void getLastSerialNumber(boolean isArmyProject, Consumer<String> success);
    void getCaseState(Long id, Consumer<CaseState> success);
}
