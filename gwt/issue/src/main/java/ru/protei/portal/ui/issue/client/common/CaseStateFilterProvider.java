package ru.protei.portal.ui.issue.client.common;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies.ALL;
import static ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies.NONE;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public abstract class CaseStateFilterProvider implements Activity
{
    private final Set<CaseState> statesSet = new HashSet<>();

    @Event
    public void onAuth(AuthEvents.Success event) {
        updateCaseStates();
    }

    @Event
    public void onUpdateItem(CaseStateEvents.UpdateItem event) {
        statesSet.add(event.caseState);
        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    public Selector.SelectorFilter<CaseState> makeFilter(List<CaseState> companyCaseStates) {
        final Set<CaseState> companiesStates = new HashSet<>();
        CollectionUtils.transform(companyCaseStates, companiesStates, Function.identity());
        return value -> {
            if (!statesSet.contains(value)) {
                return false;
            }
            if (ALL == value.getUsageInCompanies()) {
                return true;
            }
            if (NONE == value.getUsageInCompanies()) {
                return false;
            }

            return companiesStates.contains(value);
        };

    }

    private void setCaseStates(List<CaseState> states) {
        statesSet.clear();
        statesSet.addAll(emptyIfNull(states));
    }

    private void updateCaseStates() {
        caseStateService.getCaseStatesOmitPrivileges(En_CaseType.CRM_SUPPORT, new ShortRequestCallback<List<CaseState>>()
                .setOnSuccess(states -> {
                    setCaseStates(states);
                    fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                }));
    }

    @Inject
    CaseStateControllerAsync caseStateService;

    private static final Logger log = Logger.getLogger(CaseStateFilterProvider.class.getName());
}
