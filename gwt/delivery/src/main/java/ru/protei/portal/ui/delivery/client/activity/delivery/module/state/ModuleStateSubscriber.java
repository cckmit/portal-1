package ru.protei.portal.ui.delivery.client.activity.delivery.module.state;

import ru.protei.portal.core.model.ent.CaseState;

import java.util.List;

public interface ModuleStateSubscriber {

    void onStatesLoaded(List<CaseState> states);
}
