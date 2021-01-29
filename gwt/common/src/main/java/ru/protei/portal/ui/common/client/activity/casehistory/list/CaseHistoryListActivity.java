package ru.protei.portal.ui.common.client.activity.casehistory.list;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.CaseHistoryEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseHistoryControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItem;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItemsContainer;
import ru.protei.portal.ui.common.client.view.casehistory.item.casestate.CaseHistoryStateItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.importance.CaseHistoryImportanceItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.simple.CaseHistorySimpleItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.tag.CaseHistoryTagItemView;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class CaseHistoryListActivity implements AbstractCaseHistoryListActivity, Activity {
    @Event
    public void onLoad(CaseHistoryEvents.Load event) {
        event.container.clear();
        event.container.add(view.asWidget());

        requestHistoryList(event.caseId, this::fillView);
    }

    @Event
    public void onReload(CaseHistoryEvents.Reload event) {
        requestHistoryList(event.caseId, this::fillView);
    }

    private void requestHistoryList(Long caseId, Consumer<List<History>> historyListConsumer) {
        caseHistoryService.getHistoryListByCaseId(caseId, new FluentCallback<List<History>>()
                .withSuccess(historyListConsumer)
        );
    }

    private void fillView(List<History> caseHistories) {
        view.root().clear();

        String historyDate = null;
        CaseHistoryItemsContainer historyItemsContainer = null;

        List<Widget> historyItemsContainers = new LinkedList<>();

        for (History nextHistory : caseHistories) {
            if (!DateFormatter.formatDateTime(nextHistory.getDate()).equals(historyDate)) {
                historyDate = DateFormatter.formatDateTime(nextHistory.getDate());
                historyItemsContainer = caseHistoryItemsContainerProvider.get();
                historyItemsContainer.setDate(historyDate);
                historyItemsContainer.setInitiator(nextHistory.getInitiatorFullName());
                historyItemsContainers.add(0, historyItemsContainer);
            }

            addHistoryItem(nextHistory, historyItemsContainer);
        }

        historyItemsContainers.forEach(view.root()::add);
    }

    private void addHistoryItem(History history, CaseHistoryItemsContainer historyItemsContainer) {
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW)) {
            CaseHistoryItem itemView;
            switch (history.getType()) {
                case PLAN: itemView = makeHistoryItem(history, lang.plan(), Plan.class); break;
                case TAG: itemView = makeHistoryItem(history, lang.tag(), CaseTag.class); break;
                case CASE_STATE: itemView = makeHistoryItem(history, lang.issueState(), CaseState.class); break;
                case CASE_MANAGER: itemView = makeHistoryItem(history, lang.issueManager(), EmployeeShortView.class); break;
                case CASE_IMPORTANCE: itemView = makeHistoryItem(history, lang.issueImportance(), ImportanceLevel.class); break;
                default: return;
            }
            historyItemsContainer.itemsContainer().add(itemView);
        }
    }

    private CaseHistoryItem makeHistoryItem(History history, String historyType, Class<?> clazz) {
        CaseHistoryItem historyItem = caseHistoryItemProvider.get();

        historyItem.addedValueContainerVisibility().setVisible(En_HistoryAction.ADD.equals(history.getAction()));
        historyItem.changeContainerVisibility().setVisible(En_HistoryAction.CHANGE.equals(history.getAction()));
        historyItem.removedValueContainerVisibility().setVisible(En_HistoryAction.REMOVE.equals(history.getAction()));
        historyItem.setHistoryType(historyType);

        if (En_HistoryAction.ADD.equals(history.getAction())) {
            historyItem.addedValueContainer().add(makeItem(
                    history.getType(),
                    history.getNewValue(),
                    history.getNewColor(),
                    makeLink(clazz, history.getNewId()))
            );
        }

        if (En_HistoryAction.CHANGE.equals(history.getAction())) {
            historyItem.oldValueContainer().add(makeItem(
                    history.getType(),
                    history.getOldValue(),
                    history.getOldColor(),
                    makeLink(clazz, history.getOldId())
            ));

            historyItem.newValueContainer().add(makeItem(
                    history.getType(),
                    history.getNewValue(),
                    history.getNewColor(),
                    makeLink(clazz, history.getNewId())
            ));
        }

        if (En_HistoryAction.REMOVE.equals(history.getAction())) {
            historyItem.removedValueContainer().add(makeItem(
                    history.getType(),
                    history.getOldValue(),
                    history.getOldColor(),
                    makeLink(clazz, history.getOldId())
            ));
        }

        return historyItem;
    }

    private String makeLink(Class<?> clazz, Long id) {
        return LinkUtils.isLinkNeeded(clazz) ? LinkUtils.makePreviewLink(clazz, id) : null;
    }

    private Widget makeItem(En_HistoryType historyType, String name, String color, String link) {
        if (En_HistoryType.CASE_STATE.equals(historyType)) {
            CaseHistoryStateItemView caseHistoryStateItemView = caseHistoryStateItemViewProvider.get();
            caseHistoryStateItemView.setName(name);
            caseHistoryStateItemView.setColor(color);

            return caseHistoryStateItemView;
        }

        if (En_HistoryType.CASE_IMPORTANCE.equals(historyType)) {
            CaseHistoryImportanceItemView caseHistoryImportanceItemView = caseHistoryImportanceItemViewProvider.get();
            caseHistoryImportanceItemView.setName(name);
            caseHistoryImportanceItemView.setColor(color);

            return caseHistoryImportanceItemView;
        }

        if (En_HistoryType.TAG.equals(historyType)) {
            CaseHistoryTagItemView caseHistoryTagItemView = caseHistoryTagItemViewProvider.get();
            caseHistoryTagItemView.setName(name);
            caseHistoryTagItemView.setColor(color);

            return caseHistoryTagItemView;
        }

        if (En_HistoryType.CASE_MANAGER.equals(historyType) || En_HistoryType.PLAN.equals(historyType)) {
            CaseHistorySimpleItemView caseHistorySimpleItemView = caseHistorySimpleItemViewProvider.get();
            caseHistorySimpleItemView.setLink(name, link);

            return caseHistorySimpleItemView;
        }

        return null;
    }

    @Inject
    private AbstractCaseHistoryListView view;
    @Inject
    private Provider<CaseHistoryItemsContainer> caseHistoryItemsContainerProvider;
    @Inject
    private Provider<CaseHistoryItem> caseHistoryItemProvider;

    @Inject
    private Provider<CaseHistoryStateItemView> caseHistoryStateItemViewProvider;
    @Inject
    private Provider<CaseHistoryImportanceItemView> caseHistoryImportanceItemViewProvider;
    @Inject
    private Provider<CaseHistoryTagItemView> caseHistoryTagItemViewProvider;
    @Inject
    private Provider<CaseHistorySimpleItemView> caseHistorySimpleItemViewProvider;

    @Inject
    private PolicyService policyService;
    @Inject
    private CaseHistoryControllerAsync caseHistoryService;
    @Inject
    private Lang lang;
}
