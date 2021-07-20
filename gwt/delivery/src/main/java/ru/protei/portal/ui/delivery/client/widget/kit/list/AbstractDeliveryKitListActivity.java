package ru.protei.portal.ui.delivery.client.widget.kit.list;

import ru.protei.portal.core.model.ent.CaseState;

import java.util.function.Consumer;

public interface AbstractDeliveryKitListActivity {
    void getLastSerialNumber(Consumer<String> success);
    void getCaseState(Long id, Consumer<CaseState> success);
}
