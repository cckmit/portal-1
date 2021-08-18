package ru.protei.portal.ui.common.client.util;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.DeliveryStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.ModuleStateLang;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItem;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItemsContainer;
import ru.protei.portal.ui.common.client.view.casehistory.item.casestate.CaseHistoryStateItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.importance.CaseHistoryImportanceItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.simple.CaseHistorySimpleItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.tag.CaseHistoryTagItemView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static ru.protei.portal.ui.common.client.common.DateFormatter.formatDateTime;

public class CommentOrHistoryUtils {

    public static List<CommentsAndHistories.CommentOrHistory> getSortedCommentOrHistoryList(List<CommentsAndHistories.CommentOrHistory> commentOrHistoryList) {
        return commentOrHistoryList
                .stream()
                .sorted(CommentOrHistoryUtils::compareCommentOrHistoryItems)
                .collect(Collectors.toList());
    }

    public static List<CaseHistoryItemsContainer> fillView(List<History> caseHistories, FlowPanel historyContainer) {
        if (CollectionUtils.isEmpty(caseHistories)) {
            return new ArrayList<>();
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

        return currentHistoryItemsContainers;
    }

    public static String transliteration(String name) {
        return TransliterationUtils.transliterate(name, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private static CaseHistoryItem makeHistoryItem(History history) {
        switch (history.getType()) {
            case PLAN: return makeHistoryItem(history, lang.plan(), Plan.class);
            case TAG: return makeHistoryItem(history, lang.tag(), CaseTag.class);
            case CASE_STATE: return makeHistoryItem(history, lang.issueState(), CaseState.class);
            case CASE_MANAGER: return makeHistoryItem(history, lang.issueManager(), EmployeeShortView.class);
            case CASE_IMPORTANCE: return makeHistoryItem(history, lang.issueImportance(), ImportanceLevel.class);
            case DEPARTURE_DATE: return makeHistoryItem(history, lang.deliveryDepartureDate(), Delivery.class);
            case DELIVERY_STATE: return makeHistoryItem(history, lang.issueState(), Delivery.class);
            case MODULE_STATE: return makeHistoryItem(history, lang.issueState(), Module.class);
            case BUILD_DATE: return makeHistoryItem(history, lang.moduleBuildDate(), Module.class);
            default: return null;
        }
    }

    private static CaseHistoryItem makeHistoryItem(History history, String historyType, Class<?> clazz) {
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

    private static int compareCommentOrHistoryItems(CommentsAndHistories.CommentOrHistory commentOrHistory1, CommentsAndHistories.CommentOrHistory commentOrHistory2) {
        if (commentOrHistory1.getCaseComment() != null && commentOrHistory2.getCaseComment() != null) {
            return commentOrHistory1.getDate().before(commentOrHistory2.getDate()) ? -1 : 1;
        }

        if (commentOrHistory1.getHistory() != null && commentOrHistory2.getHistory() != null) {
            return commentOrHistory1.getDate().before(commentOrHistory2.getDate()) ? -1 : 1;
        }

        if (formatDateTime(commentOrHistory1.getDate()).equals(formatDateTime(commentOrHistory2.getDate()))) {
            return commentOrHistory1.getHistory() != null ? -1 : 1;
        }

        return commentOrHistory1.getDate().before(commentOrHistory2.getDate()) ? -1 : 1;
    }

    private static String makeLink(Class<?> clazz, Long id) {
        return LinkUtils.isLinkNeeded(clazz) ? LinkUtils.makePreviewLink(clazz, id) : null;
    }

    private static Widget makeItem(En_HistoryType historyType, String value, String color, String link) {
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

        if (En_HistoryType.DEPARTURE_DATE.equals(historyType)) {
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

        if (En_HistoryType.MODULE_STATE.equals(historyType)) {
            CaseHistoryStateItemView caseHistoryDeliveryStateItemView = caseHistoryStateItemViewProvider.get();
            caseHistoryDeliveryStateItemView.setName(moduleStateLang.getStateName(new CaseState(value)));
            caseHistoryDeliveryStateItemView.setColor(color);

            return caseHistoryDeliveryStateItemView;
        }

        if (En_HistoryType.BUILD_DATE.equals(historyType)) {
            CaseHistorySimpleItemView caseHistoryDateItemView = caseHistorySimpleItemViewProvider.get();
            caseHistoryDateItemView.setLink(value, null);

            return caseHistoryDateItemView;
        }

        return null;
    }

    @Inject
    private static Lang lang;
    @Inject
    private static Provider<CaseHistoryItem> caseHistoryItemProvider;
    @Inject
    private static Provider<CaseHistoryStateItemView> caseHistoryStateItemViewProvider;
    @Inject
    private static Provider<CaseHistoryImportanceItemView> caseHistoryImportanceItemViewProvider;
    @Inject
    private static Provider<CaseHistoryTagItemView> caseHistoryTagItemViewProvider;
    @Inject
    private static Provider<CaseHistorySimpleItemView> caseHistorySimpleItemViewProvider;
    @Inject
    private static CaseLinkProvider caseLinkProvider;
    @Inject
    private static DeliveryStateLang deliveryStateLang;
    @Inject
    private static ModuleStateLang moduleStateLang;
    @Inject
    private static Provider<CaseHistoryItemsContainer> caseHistoryItemsContainerProvider;
}