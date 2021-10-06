package ru.protei.portal.ui.delivery.client.widget.cardbatch.importance;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.ImportanceLevelControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class CardBatchImportanceBtnGroupModel implements Activity {
    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        refreshOptions();
    }

    public void subscribe(SelectorWithModel<ImportanceLevel> selector) {
        subscribers.add(selector);
        selector.fillOptions(importanceLevels);
    }

    private void notifySubscribers(List<ImportanceLevel> importanceLevels) {
        subscribers.forEach(subscriber -> {
            subscriber.fillOptions(importanceLevels);
            subscriber.refreshValue();
        });
    }

    private void refreshOptions() {
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.DELIVERY_VIEW)) {
            importanceService.getImportanceLevels(new FluentCallback<List<ImportanceLevel>>()
                    .withSuccess(result -> {
                        importanceLevels.clear();
                        importanceLevels.addAll(result);

                        notifySubscribers(result);
                    })
            );
        }
    }

    @Inject
    ImportanceLevelControllerAsync importanceService;

    @Inject
    PolicyService policyService;

    private final List<SelectorWithModel<ImportanceLevel>> subscribers = new ArrayList<>();
    private final List<ImportanceLevel> importanceLevels = new ArrayList<>();
}
