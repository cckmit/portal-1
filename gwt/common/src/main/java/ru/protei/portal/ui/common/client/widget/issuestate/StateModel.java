package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
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

        if (workflow == En_CaseStateWorkflow.NO_WORKFLOW) {
            if (currentCaseState.isTerminalState()) {
                return Collections.singletonList(currentCaseState);
            } else{
                return caseStatesList.stream().map(caseState -> En_CaseState.getById(caseState.getId())).collect( Collectors.toList());
            }
        }

        Map<Long, Integer> idToOrder = caseStatesList.stream().collect(Collectors.toMap(CaseState::getId, CaseState::getViewOrder));
        Map<En_CaseState, Set<En_CaseState>> flow = caseStateWorkflowList.stream()
                .filter(caseStateWorkflow -> caseStateWorkflow.isMatched(workflow))
                .flatMap(caseStateWorkflow -> caseStateWorkflow.getCaseStateWorkflowLinks().stream())
                .collect(Collectors.groupingBy(
                        CaseStateWorkflowLink::getCaseStateFrom,
                        Collectors.mapping(CaseStateWorkflowLink::getCaseStateTo,
                                Collectors.toCollection(() ->
                                        new TreeSet<>(Comparator.comparing((En_CaseState en_CaseState) -> idToOrder.get((long) en_CaseState.getId()))
                                                .thenComparing(En_CaseState::getId)
                                        )))
                ));

        return new ArrayList<>(flow.get(currentCaseState));
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateWorkflowControllerAsync caseStateWorkflowController;

    private boolean isRefreshing = false;
    private List<CaseState> caseStatesList = new ArrayList<>();
    private List<CaseStateWorkflow> caseStateWorkflowList = new ArrayList<>();
    private Map<SelectorWithModel<En_CaseState>, WorkflowWithState> subscriberMap = new HashMap<>();

    private class WorkflowWithState {
        En_CaseStateWorkflow workflow;
        En_CaseState state;
        WorkflowWithState(En_CaseStateWorkflow workflow, En_CaseState state) {
            this.workflow = workflow;
            this.state = state;
        }
    }
}
