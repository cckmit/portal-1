package ru.protei.portal.ui.common.client.activity.casehistory.list;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.view.EmployeeShortView;
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
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW)) {
            AbstractCaseHistoryItemView itemView;
            switch (history.getType()) {
                case PLAN: itemView = makeHistoryItem(history, lang.plan(), Plan.class); break;
                case TAG: itemView = makeHistoryItem(history, lang.tag(), CaseTag.class); break;
                case CASE_STATE: itemView = makeHistoryItem(history, lang.issueState(), CaseState.class); break;
                case CASE_MANAGER: itemView = makeHistoryItem(history, lang.issueManager(), EmployeeShortView.class); break;
                case CASE_IMPORTANCE: itemView = makeHistoryItem(history, lang.issueImportance(), ImportanceLevel.class); break;
                default: return;
            }
            view.root().add(itemView);
        }
    }

    private AbstractCaseHistoryItemView makeHistoryItem(History history, String historyType, Class<?> clazz) {
        AbstractCaseHistoryItemView historyItem = caseHistoryItemProvider.get();

        historyItem.setActivity(this);
        historyItem.setHistoryType(historyType);
        historyItem.setInitiator(history.getInitiator());

        historyItem.addedValueContainerVisibility().setVisible(En_HistoryAction.ADD.equals(history.getAction()));
        historyItem.changeContainerVisibility().setVisible(En_HistoryAction.CHANGE.equals(history.getAction()));
        historyItem.removedValueContainerVisibility().setVisible(En_HistoryAction.REMOVE.equals(history.getAction()));

        if (En_HistoryAction.ADD.equals(history.getAction())) {
            historyItem.setAddedValue(
                    makeLink(clazz, history.getNewId(), history.getNewValue()),
                    history.getNewValue()
            );
        }

        if (En_HistoryAction.REMOVE.equals(history.getAction())) {
            historyItem.setRemovedValue(
                    makeLink(clazz, history.getOldId(), history.getOldValue()),
                    history.getOldValue()
            );
        }

        if (En_HistoryAction.CHANGE.equals(history.getAction())) {
            historyItem.setOldValue(
                    makeLink(clazz, history.getOldId(), history.getOldValue()),
                    history.getOldValue()
            );
            historyItem.setNewValue(
                    makeLink(clazz, history.getNewId(), history.getNewValue()),
                    history.getNewValue()
            );
        }

        historyItem.setDate(DateFormatter.formatDateTime(history.getDate()));

        return historyItem;
    }

    private String makeLink(Class<?> clazz, Long id, String value) {
        if (LinkUtils.isLinkNeeded(clazz)) {
            return (new Anchor( "#" + id + " " + value, LinkUtils.makePreviewLink(clazz, id), "_blank"))
                    .toString();
        } else {
            return (new InlineLabel( "#" + id + " " + value)
                    .toString());
        }
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
