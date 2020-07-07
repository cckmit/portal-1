package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflowLink;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.CaseStateAndWorkflowList;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateWorkflowControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Модель статусов
 */
public abstract class StateModel implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        refreshData();
    }

    @Event
    public void onStateListChanged(IssueEvents.ChangeStateModel event) {
        clearData();
        refreshData();
    }

    @Event
    public void onUpdateSelectorOptions(CaseStateEvents.UpdateSelectorOptions event) {
        notifySubscribers();
    }

    public void subscribeNoWorkflow(SelectorWithModel<CaseState> selector) {
        subscribe(selector, En_CaseStateWorkflow.NO_WORKFLOW, null);
    }

    public void subscribe(SelectorWithModel<CaseState> selector, En_CaseStateWorkflow workflow, CaseState currentCaseState) {

        subscriberMap.put(selector, new WorkflowWithState(workflow, currentCaseState));

        if (isDataLoaded()) {
            List<CaseState> nextCaseStates = fetchNextCaseStatesForWorkflow(workflow, currentCaseState);
            notifySubscriber(selector, nextCaseStates);
            return;
        }

        refreshData();
    }

    private void refreshData() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        caseStateWorkflowController.getCaseStateAndWorkflowList(new FluentCallback<CaseStateAndWorkflowList>()
                .withError(throwable -> {
                    isRefreshing = false;
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(caseStateAndWorkflowList -> {
                    caseStateWorkflowList.clear();
                    caseStateWorkflowList.addAll(caseStateAndWorkflowList.getCaseStateWorkflowList());
                    caseStatesList.clear();
                    caseStatesList.addAll(caseStateAndWorkflowList.getCaseStatesList());
                    isRefreshing = false;
                    notifySubscribers();
                }));
    }

    private boolean isDataLoaded() {
        return CollectionUtils.isNotEmpty(caseStatesList) && CollectionUtils.isNotEmpty(caseStateWorkflowList);
    }

    private void clearData() {
        caseStatesList.clear();
        caseStateWorkflowList.clear();
    }

    private void notifySubscribers() {
        for (Map.Entry<SelectorWithModel<CaseState>, WorkflowWithState> entry : subscriberMap.entrySet()) {
            SelectorWithModel<CaseState> selector = entry.getKey();
            En_CaseStateWorkflow workflow = entry.getValue().workflow;
            CaseState currentCaseState = entry.getValue().state;

            List<CaseState> nextCaseStates = fetchNextCaseStatesForWorkflow(workflow, currentCaseState);
            notifySubscriber(selector, nextCaseStates);
        }
    }

    private void notifySubscriber(SelectorWithModel<CaseState> selector, List<CaseState> caseStates) {
        selector.fillOptions(caseStates);
        selector.refreshValue();
    }

    private List<CaseState> fetchNextCaseStatesForWorkflow(En_CaseStateWorkflow workflow, CaseState currentCaseState) {

        if (workflow == En_CaseStateWorkflow.NO_WORKFLOW) {
            if (currentCaseState != null && currentCaseState.isTerminal()) {
                return Collections.singletonList(currentCaseState);
            } else {
                return new ArrayList<>(caseStatesList);
            }
        }

        Set<CaseState> nextCaseStates = new HashSet<>();
        Optional<CaseState> currentState = caseStatesList.stream().filter(state -> state.equals(currentCaseState)).findFirst();
        if (currentState.isPresent()) {
            nextCaseStates.add(currentState.get());
        }

        Optional<CaseStateWorkflow> caseStateWorkflow = caseStateWorkflowList.stream()
                .filter(csw -> csw.isMatched(workflow))
                .findFirst();

        if (caseStateWorkflow.isPresent()) {
            for (CaseStateWorkflowLink caseStateWorkflowLink : caseStateWorkflow.get().getCaseStateWorkflowLinks()) {
                if (caseStateWorkflowLink.getCaseStateFromId() != currentCaseState.getId()) {
                    continue;
                }

                Optional<CaseState> caseState = caseStatesList.stream().filter(state -> state.getId() == caseStateWorkflowLink.getCaseStateToId()).findFirst();
                if (caseState.isPresent()) {
                    nextCaseStates.add(caseState.get());
                }
            }
        }

        return nextCaseStates.stream().sorted(Comparator.comparingInt(CaseState::getViewOrder)).collect(Collectors.toList());
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateWorkflowControllerAsync caseStateWorkflowController;

    private boolean isRefreshing = false;
    private List<CaseState> caseStatesList = new ArrayList<>();
    private List<CaseStateWorkflow> caseStateWorkflowList = new ArrayList<>();
    private Map<SelectorWithModel<CaseState>, WorkflowWithState> subscriberMap = new HashMap<>();

    private class WorkflowWithState {
        En_CaseStateWorkflow workflow;
        CaseState state;
        WorkflowWithState(En_CaseStateWorkflow workflow, CaseState state) {
            this.workflow = workflow;
            this.state = state;
        }
    }
}
