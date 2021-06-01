package ru.protei.portal.ui.common.client.activity.casehistory;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.CaseHistoryEvents;
import ru.protei.portal.ui.common.client.lang.DeliveryStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItem;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItemsContainer;
import ru.protei.portal.ui.common.client.view.casehistory.item.casestate.CaseHistoryStateItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.importance.CaseHistoryImportanceItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.simple.CaseHistorySimpleItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.tag.CaseHistoryTagItemView;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class CaseHistoryItemListActivity implements AbstractCaseHistoryItemListActivity, Activity {
    @Event
    public void onInit(CaseHistoryEvents.Init event) {
        this.historyContainer = event.historyContainer;
    }

    @Event
    public void onFill(CaseHistoryEvents.Fill event) {
        fillView(event.histories, historyContainer);
    }

    @Event
    public void onShow(CaseHistoryEvents.Show event) {
        historyItemsContainers.forEach(historyItemsContainer -> historyItemsContainer.setVisible(true));
    }

    @Event
    public void onHide(CaseHistoryEvents.Hide event) {
        historyItemsContainers.forEach(historyItemsContainer -> historyItemsContainer.setVisible(false));
    }

    @Event
    public void onClear(CaseHistoryEvents.Clear event) {
        historyItemsContainers.clear();
    }

    private void fillView(List<History> caseHistories, FlowPanel historyContainer) {
        if (CollectionUtils.isEmpty(caseHistories)) {
            return;
        }

        String lastHistoryDate = null;
        String lastHistoryAuthorName = null;
        CaseHistoryItemsContainer lastHistoryItemsContainer = null;

        List<CaseHistoryItemsContainer> currentHistoryItemsContainers = new LinkedList<>();

        ListIterator<History> historyListIterator = caseHistories.listIterator(caseHistories.size());

        while (historyListIterator.hasPrevious()) {
            History nextHistory = historyListIterator.previous();
            String nextHistoryDate = DateFormatter.formatDateTime(nextHistory.getDate());

            if (!nextHistoryDate.equals(lastHistoryDate) && nextHistory.getInitiatorName().equals(lastHistoryAuthorName)) {
                lastHistoryItemsContainer = caseHistoryItemsContainerProvider.get();
                lastHistoryItemsContainer.initWithoutInitiatorMode();

                lastHistoryItemsContainer.setDate(nextHistoryDate);

                lastHistoryDate = nextHistoryDate;

                currentHistoryItemsContainers.add(0, lastHistoryItemsContainer);
            }

            if (!nextHistory.getInitiatorName().equals(lastHistoryAuthorName)) {
                lastHistoryAuthorName = nextHistory.getInitiatorName();

                lastHistoryDate = nextHistoryDate;

                lastHistoryItemsContainer = caseHistoryItemsContainerProvider.get();
                lastHistoryItemsContainer.setDate(nextHistoryDate);
                lastHistoryItemsContainer.setInitiator(transliteration(lastHistoryAuthorName));

                currentHistoryItemsContainers.add(0, lastHistoryItemsContainer);
            }

            CaseHistoryItem caseHistoryItem = makeHistoryItem(nextHistory);
            if (caseHistoryItem != null) {
                lastHistoryItemsContainer.itemsContainer().add(caseHistoryItem);
            }
        }

        currentHistoryItemsContainers.forEach(itemsContainer -> historyContainer.insert(itemsContainer, 0));

        historyItemsContainers.addAll(currentHistoryItemsContainers);
    }

    private CaseHistoryItem makeHistoryItem(History history) {
        switch (history.getType()) {
            case PLAN: return makeHistoryItem(history, lang.plan(), Plan.class);
            case TAG: return makeHistoryItem(history, lang.tag(), CaseTag.class);
            case CASE_STATE: return makeHistoryItem(history, lang.issueState(), CaseState.class);
            case CASE_MANAGER: return makeHistoryItem(history, lang.issueManager(), EmployeeShortView.class);
            case CASE_IMPORTANCE: return makeHistoryItem(history, lang.issueImportance(), ImportanceLevel.class);
            case DATE: return makeHistoryItem(history, lang.deliveryDepartureDate(), Delivery.class);
            case DELIVERY_STATE: return makeHistoryItem(history, lang.issueState(), Delivery.class);
            default: return null;
        }
    }

    private CaseHistoryItem makeHistoryItem(History history, String historyType, Class<?> clazz) {
        CaseHistoryItem historyItem = caseHistoryItemProvider.get();

        historyItem.addedValueContainerVisibility().setVisible(En_HistoryAction.ADD.equals(history.getAction()));
        historyItem.changeContainerVisibility().setVisible(En_HistoryAction.CHANGE.equals(history.getAction()));
        historyItem.removedValueContainerVisibility().setVisible(En_HistoryAction.REMOVE.equals(history.getAction()));
        historyItem.setHistoryType(historyType);

        if (history.getLinkName() != null) {
            historyItem.setLinkedHistoryType(history.getLinkName(), caseLinkProvider.getLink(history.getLinkType(), history.getLinkName()));
        }

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

    private Widget makeItem(En_HistoryType historyType, String value, String color, String link) {
        if (En_HistoryType.CASE_STATE.equals(historyType)) {
            CaseHistoryStateItemView caseHistoryStateItemView = caseHistoryStateItemViewProvider.get();
            caseHistoryStateItemView.setName(value);
            caseHistoryStateItemView.setColor(color);

            return caseHistoryStateItemView;
        }

        if (En_HistoryType.CASE_IMPORTANCE.equals(historyType)) {
            CaseHistoryImportanceItemView caseHistoryImportanceItemView = caseHistoryImportanceItemViewProvider.get();
            caseHistoryImportanceItemView.setName(value);
            caseHistoryImportanceItemView.setColor(color);

            return caseHistoryImportanceItemView;
        }

        if (En_HistoryType.TAG.equals(historyType)) {
            CaseHistoryTagItemView caseHistoryTagItemView = caseHistoryTagItemViewProvider.get();
            caseHistoryTagItemView.setName(value);
            caseHistoryTagItemView.setColor(color);

            return caseHistoryTagItemView;
        }

        if (En_HistoryType.CASE_MANAGER.equals(historyType) || En_HistoryType.PLAN.equals(historyType)) {
            CaseHistorySimpleItemView caseHistorySimpleItemView = caseHistorySimpleItemViewProvider.get();
            caseHistorySimpleItemView.setLink(transliteration(value), link);

            return caseHistorySimpleItemView;
        }

        if (En_HistoryType.DATE.equals(historyType)) {
            CaseHistorySimpleItemView caseHistoryDateItemView = caseHistorySimpleItemViewProvider.get();
            caseHistoryDateItemView.setLink(value, null);

            return caseHistoryDateItemView;
        }

        if (En_HistoryType.DELIVERY_STATE.equals(historyType)) {
            CaseHistoryStateItemView caseHistoryDeliveryStateItemView = caseHistoryStateItemViewProvider.get();
            caseHistoryDeliveryStateItemView.setName(deliveryStateLang.getStateName(new CaseState(value)));
            caseHistoryDeliveryStateItemView.setColor(color);

            return caseHistoryDeliveryStateItemView;
        }

        return null;
    }

    private String transliteration(String name) {
        return TransliterationUtils.transliterate(name, LocaleInfo.getCurrentLocale().getLocaleName());
    }

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
    private CaseLinkProvider caseLinkProvider;

    @Inject
    private PolicyService policyService;
    @Inject
    private Lang lang;
    @Inject
    DeliveryStateLang deliveryStateLang;

    private FlowPanel historyContainer;
    private final List<CaseHistoryItemsContainer> historyItemsContainers = new LinkedList<>();
}
