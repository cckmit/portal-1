package ru.protei.portal.ui.common.client.common;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.util.CrmConstants;
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
        activeStateIds.add((long)CrmConstants.State.CREATED);
        activeStateIds.add((long)CrmConstants.State.OPENED);
        activeStateIds.add((long)CrmConstants.State.ACTIVE);
        activeStateIds.add((long)CrmConstants.State.TEST_LOCAL);
        activeStateIds.add((long)CrmConstants.State.WORKAROUND);
        activeStateIds.add((long)CrmConstants.State.INFO_REQUEST);
        activeStateIds.add((long)CrmConstants.State.NX_REQUEST);
        activeStateIds.add((long)CrmConstants.State.CUST_REQUEST);
        activeStateIds.add((long)CrmConstants.State.CUST_PENDING);
        activeStateIds.add((long)CrmConstants.State.TEST_CUST);

        newStateIds = new ArrayList<>(3);
        newStateIds.add((long)CrmConstants.State.CREATED);
        newStateIds.add((long)CrmConstants.State.OPENED);
        newStateIds.add((long)CrmConstants.State.ACTIVE);

        filterCaseResolutionTimeActiveStateIds = new ArrayList<>(8);
        filterCaseResolutionTimeActiveStateIds.add((long)CrmConstants.State.CREATED);
        filterCaseResolutionTimeActiveStateIds.add((long)CrmConstants.State.OPENED);
        filterCaseResolutionTimeActiveStateIds.add((long)CrmConstants.State.ACTIVE);
        filterCaseResolutionTimeActiveStateIds.add((long)CrmConstants.State.TEST_LOCAL);
        filterCaseResolutionTimeActiveStateIds.add((long)CrmConstants.State.WORKAROUND);
        filterCaseResolutionTimeActiveStateIds.add((long)CrmConstants.State.INFO_REQUEST);
        filterCaseResolutionTimeActiveStateIds.add((long)CrmConstants.State.NX_REQUEST);
        filterCaseResolutionTimeActiveStateIds.add((long)CrmConstants.State.CUST_REQUEST);
    }

    @Event
    public void authEvent(AuthEvents.Success event) {
        caseStateController.getCaseState((long)CrmConstants.State.CREATED, new FluentCallback<CaseState>()
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
