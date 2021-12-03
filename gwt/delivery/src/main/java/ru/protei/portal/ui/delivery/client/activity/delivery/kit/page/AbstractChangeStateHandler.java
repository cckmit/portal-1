package ru.protei.portal.ui.delivery.client.activity.delivery.kit.page;

import ru.protei.portal.core.model.ent.CaseState;

public interface AbstractChangeStateHandler {

    void onModulesStateChangeClicked(CaseState caseState);
}
