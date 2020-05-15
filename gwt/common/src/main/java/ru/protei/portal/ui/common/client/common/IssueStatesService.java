package ru.protei.portal.ui.common.client.common;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Статусы обращений
 */
public abstract class IssueStatesService implements Activity {

    @Inject
    public void onInit() {
        activeStateIds = new ArrayList<>(10);
        activeStateIds.add((long)En_CaseState.CREATED.getId());
        activeStateIds.add((long)En_CaseState.OPENED.getId());
        activeStateIds.add((long)En_CaseState.ACTIVE.getId());
        activeStateIds.add((long)En_CaseState.TEST_LOCAL.getId());
        activeStateIds.add((long)En_CaseState.WORKAROUND.getId());
        activeStateIds.add((long)En_CaseState.INFO_REQUEST.getId());
        activeStateIds.add((long)En_CaseState.NX_REQUEST.getId());
        activeStateIds.add((long)En_CaseState.CUST_REQUEST.getId());
        activeStateIds.add((long)En_CaseState.CUST_PENDING.getId());
        activeStateIds.add((long)En_CaseState.TEST_CUST.getId());

        newStateIds = new ArrayList<>(3);
        newStateIds.add((long)En_CaseState.CREATED.getId());
        newStateIds.add((long)En_CaseState.OPENED.getId());
        newStateIds.add((long)En_CaseState.ACTIVE.getId());

        filterCaseResolutionTimeActiveStateIds = new ArrayList<>(8);
        filterCaseResolutionTimeActiveStateIds.add((long)En_CaseState.CREATED.getId());
        filterCaseResolutionTimeActiveStateIds.add((long)En_CaseState.OPENED.getId());
        filterCaseResolutionTimeActiveStateIds.add((long)En_CaseState.ACTIVE.getId());
        filterCaseResolutionTimeActiveStateIds.add((long)En_CaseState.TEST_LOCAL.getId());
        filterCaseResolutionTimeActiveStateIds.add((long)En_CaseState.WORKAROUND.getId());
        filterCaseResolutionTimeActiveStateIds.add((long)En_CaseState.INFO_REQUEST.getId());
        filterCaseResolutionTimeActiveStateIds.add((long)En_CaseState.NX_REQUEST.getId());
        filterCaseResolutionTimeActiveStateIds.add((long)En_CaseState.CUST_REQUEST.getId());
    }

    @Event
    public void authEvent(AuthEvents.Success event) {
        caseStateController.getCaseState((long)En_CaseState.CREATED.getId(), new FluentCallback<CaseState>()
                .withSuccess(this::setCreatedCaseState));
    }

    public List<Long> getActiveStateIds() {
        return activeStateIds;
    }

    public List<Long> getNewStateIds() {
        return newStateIds;
    }

    public Set<CaseState> getFilterCaseResolutionTimeActiveStates() {
        return IssueFilterUtils.getStates(filterCaseResolutionTimeActiveStateIds);
    }

    public CaseState getCreatedCaseState() {
        return createdCaseState;
    }

    private void setCreatedCaseState(CaseState createdCaseState) {
        this.createdCaseState = createdCaseState;
    }

    @Inject
    CaseStateControllerAsync caseStateController;

    private List<Long> activeStateIds;
    private List<Long> newStateIds;
    private List<Long> filterCaseResolutionTimeActiveStateIds;
    private CaseState createdCaseState;
}
