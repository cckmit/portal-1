package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflow;
import ru.protei.portal.core.model.ent.CaseStateWorkflowLink;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateWorkflowControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;

/**
 * Модель статусов
 */
public abstract class StateModel implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clearData();
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

    public void subscribe(En_CaseStateWorkflow workflow, SelectorWithModel<En_CaseState> selector) {

        if (!subscriberMap.containsKey(workflow)) {
            subscriberMap.put(workflow, new ArrayList<>());
        } else {
            unsubscribe(selector);
        }

        subscriberMap.get(workflow).add(selector);

        if (isDataLoaded()) {
            List<En_CaseState> caseStates = fetchCaseStatesForWorkflow(workflow);
            notifySubscriber(selector, caseStates);
            return;
        }

        refreshData();
    }

    public void unsubscribe(SelectorWithModel<En_CaseState> selector) {
        subscriberMap.values().forEach(subscribers -> {
            subscribers.remove(selector);
        });
    }

    private void refreshData() {
        issueController.getStateList(new FluentCallback<List<En_CaseState>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(caseStateList -> {
                    caseStatesList.clear();
                    caseStatesList.addAll(caseStateList);
                    refreshWorkflow();
                }));
    }

    private void refreshWorkflow() {
        caseStateWorkflowController.getWorkflowList(new FluentCallback<List<CaseStateWorkflow>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(workflowList -> {
                    caseStateWorkflowList.clear();
                    caseStateWorkflowList.addAll(workflowList);
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
        for (Map.Entry<En_CaseStateWorkflow, List<SelectorWithModel<En_CaseState>>> entry : subscriberMap.entrySet()) {
            En_CaseStateWorkflow workflow = entry.getKey();
            List<SelectorWithModel<En_CaseState>> subscribers = entry.getValue();

            List<En_CaseState> caseStates = fetchCaseStatesForWorkflow(workflow);
            for (SelectorWithModel<En_CaseState> selector : subscribers) {
                notifySubscriber(selector, caseStates);
            }
        }
    }

    private void notifySubscriber(SelectorWithModel<En_CaseState> selector, List<En_CaseState> caseStates) {
        selector.fillOptions(caseStates);
        selector.refreshValue();
    }

    private List<En_CaseState> fetchCaseStatesForWorkflow(En_CaseStateWorkflow workflow) {

        if (workflow == En_CaseStateWorkflow.NO_WORKFLOW) {
            return caseStatesList;
        }

        Optional<CaseStateWorkflow> caseStateWorkflow = caseStateWorkflowList.stream()
                .filter(csw -> csw.getId() == workflow.getId())
                .findFirst();

        if (caseStateWorkflow.isPresent()) {
            Set<En_CaseState> supportedCaseStates = new HashSet<>();
            for (CaseStateWorkflowLink caseStateWorkflowLink : caseStateWorkflow.get().getCaseStateWorkflowLinks()) {
                if (caseStatesList.contains(caseStateWorkflowLink.getCaseStateFrom())) {
                    supportedCaseStates.add(caseStateWorkflowLink.getCaseStateFrom());
                }
                if (caseStatesList.contains(caseStateWorkflowLink.getCaseStateTo())) {
                    supportedCaseStates.add(caseStateWorkflowLink.getCaseStateTo());
                }
            }
            return CollectionUtils.toList(supportedCaseStates, caseState -> caseState);
        } else {
            return new ArrayList<>();
        }
    }

    @Inject
    Lang lang;
    @Inject
    IssueControllerAsync issueController;
    @Inject
    CaseStateWorkflowControllerAsync caseStateWorkflowController;

    private List<En_CaseState> caseStatesList = new ArrayList<>();
    private List<CaseStateWorkflow> caseStateWorkflowList = new ArrayList<>();
    private Map<En_CaseStateWorkflow, List<SelectorWithModel<En_CaseState>>> subscriberMap = new HashMap<>();
}
