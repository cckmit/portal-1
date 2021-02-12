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
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.service.CaseStateWorkflowControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.util.CaseStateUtil.isTerminalState;

/**
 * Модель статусов
 */
public abstract class StateModel implements Activity, AsyncSelectorModel<CaseState>, SelectorItemRenderer<CaseState> {

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        clearData();
    }

    @Event
    public void onStateListChanged(IssueEvents.ChangeStateModel event) {
        clearData();
    }

    @Override
    public CaseState get(int elementIndex, LoadingHandler selector) {
        if (!isDataLoaded()) {
            selector.onLoadingStart();
            requestData(selector);
            return null;
        }

        return CollectionUtils.get(
                workflowWithStateMap.computeIfAbsent(new WorkflowWithState(workflow, currentCaseState),
                        workflowWithState -> fetchNextCaseStatesForWorkflow(workflow, currentCaseState)),
                elementIndex);
    }

    @Override
    public String getElementName(CaseState value) {
        return value == null ? "" : value.getState();
    }

    protected void requestData(LoadingHandler selector) {
        caseStateWorkflowController.getCaseStateAndWorkflowList(new FluentCallback<CaseStateAndWorkflowList>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(caseStateAndWorkflowList -> {
                    workflowWithStateMap.clear();
                    caseStateWorkflowList.clear();
                    caseStateWorkflowList.addAll(caseStateAndWorkflowList.getCaseStateWorkflowList());
                    caseStatesList.clear();
                    caseStatesList.addAll(caseStateAndWorkflowList.getCaseStatesList());

                    if (selector != null){
                        selector.onLoadingComplete();
                    }
                }));
    }

    public void setCurrentCaseState(CaseState caseState) {
        this.currentCaseState = caseState;
    }

    public void setWorkflow(En_CaseStateWorkflow workflow) {
        this.workflow = workflow;
    }

    public En_CaseStateWorkflow getWorkflow() {
        return this.workflow;
    }

    private boolean isDataLoaded() {
        return CollectionUtils.isNotEmpty(caseStatesList) && CollectionUtils.isNotEmpty(caseStateWorkflowList);
    }

    private void clearData() {
        caseStatesList.clear();
        caseStateWorkflowList.clear();
        workflowWithStateMap.clear();
    }

    private List<CaseState> fetchNextCaseStatesForWorkflow(En_CaseStateWorkflow workflow, CaseState currentCaseState) {

        if (En_CaseStateWorkflow.NO_WORKFLOW.equals(workflow)) {
            if (currentCaseState != null && isTerminalState(currentCaseState.getId())) {
                return Collections.singletonList(caseStatesList.stream().filter(state -> state.equals(currentCaseState)).findFirst().orElse(currentCaseState));
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

    private En_CaseStateWorkflow workflow;
    private CaseState currentCaseState;

    private List<CaseState> caseStatesList = new ArrayList<>();
    private List<CaseStateWorkflow> caseStateWorkflowList = new ArrayList<>();
    private Map<WorkflowWithState, List<CaseState>> workflowWithStateMap = new HashMap();

    private class WorkflowWithState {
        En_CaseStateWorkflow workflow;
        CaseState state;
        WorkflowWithState(En_CaseStateWorkflow workflow, CaseState state) {
            this.workflow = workflow;
            this.state = state;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkflowWithState workflowWithState = (WorkflowWithState) o;
            return Objects.equals(workflowWithState.workflow, this.workflow) && Objects.equals(workflowWithState.state, this.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(workflow, state);
        }
    }
}
