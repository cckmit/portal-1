package ru.protei.portal.ui.common.client.activity.casehistory.list;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.ent.Plan;
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
            if (En_HistoryType.PLAN.equals(history.getType())) {
                view.root().add(makeHistoryItem(history, lang.plan(), Plan.class.getSimpleName()));
            }

            if (En_HistoryType.TAG.equals(history.getType())) {
                view.root().add(makeHistoryItem(history, lang.tag(), CaseTag.class.getSimpleName()));
            }

            if (En_HistoryType.CASE_STATE.equals(history.getType())) {
                view.root().add(makeHistoryItem(history, lang.issueState(), "CaseState"));
            }

            if (En_HistoryType.CASE_MANAGER.equals(history.getType())) {
                view.root().add(makeHistoryItem(history, lang.issueManager(), "CaseManager"));
            }

            if (En_HistoryType.CASE_IMPORTANCE.equals(history.getType())) {
                view.root().add(makeHistoryItem(history, lang.issueImportance(), "CaseImportance"));
            }
        }
    }

    private AbstractCaseHistoryItemView makeHistoryItem(History history, String historyType, String classSimpleName) {
        AbstractCaseHistoryItemView historyItem = caseHistoryItemProvider.get();

        historyItem.setActivity(this);
        historyItem.setHistoryType(historyType);
        historyItem.setInitiator(history.getInitiator());

        historyItem.addedValueContainerVisibility().setVisible(En_HistoryAction.ADD.equals(history.getAction()));
        historyItem.changeContainerVisibility().setVisible(En_HistoryAction.CHANGE.equals(history.getAction()));
        historyItem.removedValueContainerVisibility().setVisible(En_HistoryAction.REMOVE.equals(history.getAction()));

        if (En_HistoryAction.ADD.equals(history.getAction())) {
            historyItem.setAddedValue(
                    makeLink(classSimpleName, history.getNewId(), history.getNewValue()),
                    history.getNewValue()
            );
        }

        if (En_HistoryAction.REMOVE.equals(history.getAction())) {
            historyItem.setRemovedValue(
                    makeLink(classSimpleName, history.getOldId(), history.getOldValue()),
                    history.getOldValue()
            );
        }

        if (En_HistoryAction.CHANGE.equals(history.getAction())) {
            historyItem.setOldValue(
                    makeLink(classSimpleName, history.getOldId(), history.getOldValue()),
                    history.getOldValue()
            );
            historyItem.setNewValue(
                    makeLink(classSimpleName, history.getNewId(), history.getNewValue()),
                    history.getNewValue()
            );
        }

        historyItem.setDate(DateFormatter.formatDateTime(history.getDate()));

        return historyItem;
    }

    private String makeLink(String classSimpleName, Long id, String value) {
        if (LinkUtils.isLinkNeeded(classSimpleName)) {
            return (new Anchor( "#" + id + " " + value, LinkUtils.makePreviewLink(classSimpleName, id), "_blank"))
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
