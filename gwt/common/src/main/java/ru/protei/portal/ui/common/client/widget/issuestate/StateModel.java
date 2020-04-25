package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflowLink;
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

import static java.util.stream.Collectors.*;
import static ru.protei.portal.core.model.dict.En_CaseStateWorkflow.NO_WORKFLOW;

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

    public void subscribeNoWorkflow(SelectorWithModel<En_CaseState> selector) {
        subscribe(selector, En_CaseStateWorkflow.NO_WORKFLOW, null);
    }

    public void subscribe(SelectorWithModel<En_CaseState> selector, En_CaseStateWorkflow workflow, En_CaseState currentCaseState) {

        subscriberMap.put(selector, new WorkflowWithState(workflow, currentCaseState));

        if (isDataLoaded()) {
            List<En_CaseState> nextCaseStates = fetchNextCaseStatesForWorkflow(workflow, currentCaseState);
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
                    isRefreshing = false;
                    workflowCaseStateMap = createWorkflowCaseStateMap(
                            caseStateAndWorkflowList.getCaseStatesList(),
                            caseStateAndWorkflowList.getCaseStateWorkflowList());
                    notifySubscribers();
                }));
    }

    private Map<En_CaseStateWorkflow, Map<En_CaseState, List<En_CaseState>>> createWorkflowCaseStateMap(
            List<CaseState> caseStatesList, List<CaseStateWorkflow> caseStateWorkflowList) {
        Map<En_CaseStateWorkflow, Map<En_CaseState, List<En_CaseState>>> workflowCaseStateMap = new HashMap<>();

        workflowCaseStateMap.put(NO_WORKFLOW, createNoWorkflowMap(caseStatesList));

        Map<Long, Integer> idToOrder =
                caseStatesList.stream().collect(
                        toMap(CaseState::getId, CaseState::getViewOrder));
        Comparator<En_CaseState> enCaseStateComparator =
                Comparator.comparing((En_CaseState en_CaseState) -> idToOrder.get((long) en_CaseState.getId()))
                        .thenComparing(En_CaseState::getId);
        Map<Long, List<CaseStateWorkflowLink>> caseStateWorkflowListMap =
                caseStateWorkflowList.stream().collect(
                        toMap(CaseStateWorkflow::getId, CaseStateWorkflow::getCaseStateWorkflowLinks));

        for (En_CaseStateWorkflow en_caseStateWorkflow : En_CaseStateWorkflow.values()) {
            if (en_caseStateWorkflow == NO_WORKFLOW) {
                continue;
            }
            workflowCaseStateMap.put(en_caseStateWorkflow,
                    createOtherWorkflowMap(enCaseStateComparator, caseStateWorkflowListMap.get((long) en_caseStateWorkflow.getId())));
        }

        return workflowCaseStateMap;
    }

    private Map<En_CaseState, List<En_CaseState>> createNoWorkflowMap(List<CaseState> caseStatesList) {
        Map<En_CaseState, List<En_CaseState>> noWorkflowMap = new HashMap<>();
        List<En_CaseState> noWorkflowList = caseStatesList.stream().map(
                caseState -> En_CaseState.getById(caseState.getId()))
                .collect( toList());
        for (En_CaseState currentCaseState : En_CaseState.values()) {
            if (!currentCaseState.isTerminalState()) {
                noWorkflowMap.put(currentCaseState, noWorkflowList);
            }
        }
        return noWorkflowMap;
    }

    private  Map<En_CaseState, List<En_CaseState>> createOtherWorkflowMap(
            Comparator<En_CaseState> comparator, List<CaseStateWorkflowLink> caseStateWorkflowList) {
        Map<En_CaseState, Set<En_CaseState>> flow = caseStateWorkflowList.stream()
                .collect(groupingBy(
                        CaseStateWorkflowLink::getCaseStateFrom,
                        mapping(CaseStateWorkflowLink::getCaseStateTo, toCollection(() -> new TreeSet<>(comparator)))
                ));

        Map<En_CaseState, List<En_CaseState>> workflowMap = new HashMap<>();
        for (En_CaseState en_caseState : flow.keySet()) {
            workflowMap.put(en_caseState, new ArrayList<>(flow.get(en_caseState)));
        }

        return workflowMap;
    }

    private boolean isDataLoaded() {
        return workflowCaseStateMap != null;
    }

    private void clearData() {
        workflowCaseStateMap = null;
    }

    private void notifySubscribers() {
        for (Map.Entry<SelectorWithModel<En_CaseState>, WorkflowWithState> entry : subscriberMap.entrySet()) {
            SelectorWithModel<En_CaseState> selector = entry.getKey();
            En_CaseStateWorkflow workflow = entry.getValue().workflow;
            En_CaseState currentCaseState = entry.getValue().state;

            List<En_CaseState> nextCaseStates = fetchNextCaseStatesForWorkflow(workflow, currentCaseState);
            notifySubscriber(selector, nextCaseStates);
        }
    }

    private void notifySubscriber(SelectorWithModel<En_CaseState> selector, List<En_CaseState> caseStates) {
        selector.fillOptions(caseStates);
        selector.refreshValue();
    }

    private List<En_CaseState> fetchNextCaseStatesForWorkflow(En_CaseStateWorkflow workflow, En_CaseState currentCaseState) {
        return workflowCaseStateMap.get(workflow).getOrDefault(currentCaseState, new ArrayList<>());
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateWorkflowControllerAsync caseStateWorkflowController;

    private boolean isRefreshing = false;
    private Map<SelectorWithModel<En_CaseState>, WorkflowWithState> subscriberMap = new HashMap<>();
    private Map<En_CaseStateWorkflow, Map<En_CaseState, List<En_CaseState>>> workflowCaseStateMap;

    private class WorkflowWithState {
        En_CaseStateWorkflow workflow;
        En_CaseState state;
        WorkflowWithState(En_CaseStateWorkflow workflow, En_CaseState state) {
            this.workflow = workflow;
            this.state = state;
        }
    }
}
