package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.*;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies.ALL;
import static ru.protei.portal.core.model.ent.En_CaseStateUsageInCompanies.NONE;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

public abstract class CaseStateFilterProvider implements Activity
{
    private final Map<En_CaseState,CaseState> statesMap = new HashMap<>();

    @Event
    public void onAuth(AuthEvents.Success event) {
        updateCaseStates();
    }

    @Event
    public void onUpdateItem(CaseStateEvents.UpdateItem event) {
        statesMap.replace(CaseState.asState(event.caseState), event.caseState);
        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    public Selector.SelectorFilter<En_CaseState> makeFilter(List<CaseState> companyCaseStates) {
        final Set<En_CaseState> companiesStates = new HashSet<>();
        CollectionUtils.transform(companyCaseStates, companiesStates, input -> CaseState.asState(input));
        return new Selector.SelectorFilter<En_CaseState>(){
            @Override
            public boolean isDisplayed(En_CaseState value) {
                if (!statesMap.containsKey(value)) {
                    log.config("isDisplayed(): False emptyMap " + value );//TODO DEBUG
                    return false;
                }
                if (ALL == statesMap.get(value).getUsageInCompanies()) {
                    log.config("isDisplayed(): Try ALL " + value );//TODO DEBUG
                    return true;
                }
                if (NONE == statesMap.get(value).getUsageInCompanies()) {
                    log.config("isDisplayed(): False NONE " + value );//TODO DEBUG
                    return false;
                }

                boolean c2 = companiesStates.contains(value);
                log.config("isDisplayed(): "+c2+" companins " + value );//TODO DEBUG
                return c2;
            }
        };

    }


    private void setCaseStates(List<CaseState> states) {
        statesMap.clear();
        for (CaseState state: emptyIfNull(states)) {
            statesMap.put(CaseState.asState(state), state);
        }
    }

    private void updateCaseStates() {
        caseStateService.getCaseStates(new ShortRequestCallback<List<CaseState>>()
                .setOnSuccess(states -> {
                    setCaseStates(states);
                    fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                }));
    }


    @Inject
    CaseStateControllerAsync caseStateService;

    private static final Logger log = Logger.getLogger(CaseStateFilterProvider.class.getName());
}
