package ru.protei.portal.ui.delivery.client.activity.delivery.kit.handler;

import ru.protei.portal.core.model.ent.CaseState;

public interface KitActionsHandler {
    void onCopy();
    void onGroupChangeState(CaseState state);
    void onGroupRemove();
    void onBack();
    void onEdit();
}
