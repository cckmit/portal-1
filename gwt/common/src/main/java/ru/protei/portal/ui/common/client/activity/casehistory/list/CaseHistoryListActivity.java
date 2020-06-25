package ru.protei.portal.ui.common.client.activity.casehistory.list;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_HistoryValueType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.casehistory.item.AbstractCaseHistoryItemActivity;
import ru.protei.portal.ui.common.client.activity.casehistory.item.AbstractCaseHistoryItemView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.CaseHistoryEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseHistoryControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.CollectionUtils.forEachReverse;

public abstract class CaseHistoryListActivity implements AbstractCaseHistoryListActivity, AbstractCaseHistoryItemActivity, Activity {
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
        forEachReverse(caseHistories, this::addHistoryItem);
    }

    private void addHistoryItem(History history) {
        if (En_HistoryValueType.PLANS.contains(history.getValueType()) && policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW)) {
            view.root().add(makePlanHistoryItem(history).asWidget());
        }
    }

    private AbstractCaseHistoryItemView makePlanHistoryItem(History history) {
        AbstractCaseHistoryItemView historyItem = caseHistoryItemProvider.get();

        historyItem.setActivity(this);
        historyItem.setHistoryType(lang.plan());

        historyItem.addedValueContainerVisibility().setVisible(En_HistoryValueType.ADD_TO_PLAN.equals(history.getValueType()));
        historyItem.changeContainerVisibility().setVisible(En_HistoryValueType.CHANGE_PLAN.equals(history.getValueType()));
        historyItem.removedValueContainerVisibility().setVisible(En_HistoryValueType.REMOVE_FROM_PLAN.equals(history.getValueType()));

        if (En_HistoryValueType.ADD_TO_PLAN.equals(history.getValueType())) {
            historyItem.setAddedValue(
                    makeLink(Plan.class, history.getNewValue()),
                    history.getNewValue().getDisplayText()
            );
        }

        if (En_HistoryValueType.REMOVE_FROM_PLAN.equals(history.getValueType())) {
            historyItem.setRemovedValue(
                    makeLink(Plan.class, history.getOldValue()),
                    history.getOldValue().getDisplayText()
            );
        }

        if (En_HistoryValueType.CHANGE_PLAN.equals(history.getValueType())) {
            historyItem.setOldValue(
                    makeLink(Plan.class, history.getOldValue()),
                    history.getOldValue().getDisplayText()
            );
            historyItem.setNewValue(
                    makeLink(Plan.class, history.getNewValue()),
                    history.getNewValue().getDisplayText()
            );
        }

        historyItem.setDate(DateFormatter.formatDateTime(history.getDate()));

        return historyItem;
    }

    private String makeLink(Class<?> clazz, EntityOption option) {
        return "<a target='_blank' " +
                "href='" + LinkUtils.makePreviewLink(clazz, option.getId()) + "'>" +
                "#" + option.getId() + " " + option.getDisplayText() +
                "</a>";
    }

    @Inject
    private AbstractCaseHistoryListView view;
    @Inject
    private Provider<AbstractCaseHistoryItemView> caseHistoryItemProvider;

    @Inject
    private PolicyService policyService;
    @Inject
    private CaseHistoryControllerAsync caseHistoryService;
    @Inject
    private Lang lang;
}
