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
                    workflowCaseStateEdges = createWorkflowCaseStateEdges(
                            caseStateAndWorkflowList.getCaseStatesList(),
                            caseStateAndWorkflowList.getCaseStateWorkflowList());
                    notifySubscribers();
                }));
    }

    private Map<En_CaseStateWorkflow, Edges> createWorkflowCaseStateEdges(
            List<CaseState> caseStatesList, List<CaseStateWorkflow> caseStateWorkflowList) {
        Map<En_CaseStateWorkflow, Edges> workflowToEdges = new HashMap<>();

        workflowToEdges.put(NO_WORKFLOW, createNoWorkflowMap(caseStatesList));

        Map<Long, List<CaseStateWorkflowLink>> idToWorkflowList =
                caseStateWorkflowList.stream().collect(
                        toMap(CaseStateWorkflow::getId, CaseStateWorkflow::getCaseStateWorkflowLinks));

        Comparator<En_CaseState> stateComparator = createStateComparator(caseStatesList);
        for (En_CaseStateWorkflow en_caseStateWorkflow : En_CaseStateWorkflow.values()) {
            if (en_caseStateWorkflow == NO_WORKFLOW) {
                continue;
            }
            workflowToEdges.put(en_caseStateWorkflow,
                    createOtherWorkflowMap(stateComparator, idToWorkflowList.get((long) en_caseStateWorkflow.getId())));
        }

        return workflowToEdges;
    }

    private Comparator<En_CaseState> createStateComparator(List<CaseState> caseStatesList) {
        Map<Long, Integer> idToOrder =
                caseStatesList.stream().collect(
                        toMap(CaseState::getId, CaseState::getViewOrder));
        return Comparator.comparing((En_CaseState en_CaseState) -> idToOrder.get((long) en_CaseState.getId()))
                .thenComparing(En_CaseState::getId);
    }

    private Edges createNoWorkflowMap(List<CaseState> caseStatesList) {

        List<En_CaseState> noWorkflowList = caseStatesList.stream().map(
                caseState -> En_CaseState.getById(caseState.getId()))
                .collect( toList());

        Edges noWorkflowEdges = new Edges();
        for (En_CaseState currentCaseState : En_CaseState.values()) {
            if (!currentCaseState.isTerminalState()) {
                noWorkflowEdges.addEdges(currentCaseState, noWorkflowList);
            }
        }
        return noWorkflowEdges;
    }

    private Edges createOtherWorkflowMap(Comparator<En_CaseState> comparator,
            List<CaseStateWorkflowLink> caseStateWorkflowList) {

        Map<En_CaseState, Set<En_CaseState>> flowWithStateToOrder = caseStateWorkflowList.stream()
                .collect(groupingBy(
                        CaseStateWorkflowLink::getCaseStateFrom,
                        mapping(CaseStateWorkflowLink::getCaseStateTo, toCollection(() -> new TreeSet<>(comparator)))
                ));

        Edges workflowEdges = new Edges();
        for (En_CaseState en_caseState : flowWithStateToOrder.keySet()) {
            workflowEdges.addEdges(en_caseState, new ArrayList<>(flowWithStateToOrder.get(en_caseState)));
        }

        return workflowEdges;
    }

    private boolean isDataLoaded() {
        return workflowCaseStateEdges != null;
    }

    private void clearData() {
        workflowCaseStateEdges = null;
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
        return workflowCaseStateEdges.get(workflow).getTo(currentCaseState);
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateWorkflowControllerAsync caseStateWorkflowController;

    private boolean isRefreshing = false;
    private Map<SelectorWithModel<En_CaseState>, WorkflowWithState> subscriberMap = new HashMap<>();
    private Map<En_CaseStateWorkflow, Edges> workflowCaseStateEdges;

    private static class WorkflowWithState {
        En_CaseStateWorkflow workflow;
        En_CaseState state;
        WorkflowWithState(En_CaseStateWorkflow workflow, En_CaseState state) {
            this.workflow = workflow;
            this.state = state;
        }
    }

    private static class Edges {
        Map<En_CaseState, List<En_CaseState>> map = new HashMap<>();

        void addEdges(En_CaseState from, List<En_CaseState> to) {
            map.put(from, to);
        }

        List<En_CaseState> getTo(En_CaseState from) {
            return map.getOrDefault(from, new ArrayList<>());
        }
    }
}
