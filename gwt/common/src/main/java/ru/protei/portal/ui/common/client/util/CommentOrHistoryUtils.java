package ru.protei.portal.ui.common.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.CommentAndHistoryEvents;
import ru.protei.portal.ui.common.client.lang.*;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItem;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItemsContainer;
import ru.protei.portal.ui.common.client.view.casehistory.item.casestate.CaseHistoryStateItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.importance.CaseHistoryImportanceItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.link.CaseHistoryLinkItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.simple.CaseHistorySimpleItemView;
import ru.protei.portal.ui.common.client.view.casehistory.item.tag.CaseHistoryTagItemView;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.protei.portal.ui.common.client.common.DateFormatter.formatDateTime;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;

public class CommentOrHistoryUtils {

    static Activity activity;

    public static List<CommentsAndHistories.CommentOrHistory> getSortedCommentOrHistoryList(List<CommentsAndHistories.CommentOrHistory> commentOrHistoryList) {
        return commentOrHistoryList
                .stream()
                .sorted(CommentOrHistoryUtils::compareCommentOrHistoryItems)
                .collect(Collectors.toList());
    }

    public static List<CaseHistoryItemsContainer> fillView(List<History> caseHistories, FlowPanel historyContainer) {
        return fillView(null, caseHistories, historyContainer);
    }

    public static List<CaseHistoryItemsContainer> fillView(Activity activity, List<History> caseHistories, FlowPanel historyContainer) {
        CommentOrHistoryUtils.activity = activity;
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
            case CARD_STATE: return makeHistoryItem(history, lang.cardState(), CaseState.class);
            case CARD_MANAGER: return makeHistoryItem(history, lang.cardManager(), EmployeeShortView.class);
            case CARD_BATCH_STATE: return makeHistoryItem(history, lang.cardBatchState(), CaseState.class);
            case CARD_BATCH_IMPORTANCE: return makeHistoryItem(history, lang.issueImportance(), ImportanceLevel.class);
            case CASE_PAUSE_DATE: return makeHistoryItem(history, lang.issuePauseDateValidity(), Long.class);
            case CASE_PRODUCT: return makeHistoryItem(history, lang.issueProduct(), DevUnit.class);
            case CASE_DEADLINE: return makeHistoryItem(history, lang.issueDeadline(), Long.class);
            case CASE_WORK_TRIGGER: return makeHistoryItem(history, lang.issueWorkTrigger(), En_WorkTrigger.class);
            case CASE_MANAGER_COMPANY: return makeHistoryItem(history, lang.issueCompany(), Company.class);
            case CASE_INITIATOR_COMPANY: return makeHistoryItem(history, lang.issueInitiatorCompany(), Company.class);
            case CASE_INITIATOR: return makeHistoryItem(history, lang.issueInitiator(), EmployeeShortView.class);
            case CASE_PLATFORM: return makeHistoryItem(history, lang.siteFolderPlatform(), Platform.class);
            case CASE_NAME: return makeHistoryItem(history, lang.issueName(), String.class);
            case CASE_INFO: return makeHistoryItem(history, lang.description(), String.class);
            case CASE_ATTACHMENT: return makeHistoryItem(history, lang.attachment(), CaseAttachment.class);
            case CASE_LINK: return makeHistoryItem(history, getLinkBundleName(history), CaseLink.class);
            default: return null;
        }
    }

    private static String getLinkBundleName(History history) {

        if (En_HistoryAction.ADD.equals(history.getAction())
                || En_HistoryAction.CHANGE.equals(history.getAction())){
            En_BundleType bundleType = CaseLinkProvider.getBundleType(history.getNewId());
            if (bundleType == null){
                return lang.linkedWith();
            }
            return bundleTypeLang.getName(bundleType);
        }
        return lang.linkRemoved();
    }

    private static CaseHistoryItem makeHistoryItem(History history, String historyType, Class<?> clazz) {
        CaseHistoryItem historyItem = caseHistoryItemProvider.get();

        historyItem.addedValueContainerVisibility().setVisible(En_HistoryAction.ADD.equals(history.getAction()));
        historyItem.changeContainerVisibility().setVisible(En_HistoryAction.CHANGE.equals(history.getAction()));
        historyItem.removedValueContainerVisibility().setVisible(En_HistoryAction.REMOVE.equals(history.getAction()));
        historyItem.oldValueContainerVisibility().setVisible(!isCaseInfoType(history.getType()));
        historyItem.setChangeValueIconVisible(!isCaseInfoType(history.getType()));
        historyItem.setHistoryType(historyType);

        if (history.getLinkName() != null) {
            historyItem.setLinkedHistoryType(history.getLinkName(), caseLinkProvider.getLink(history.getLinkType(), history.getLinkName()));
        }

        if (En_HistoryAction.ADD.equals(history.getAction())) {
            historyItem.addedValueContainer().add(makeItem(
                    history.getId(),
                    history.getAction(),
                    history.getType(),
                    history.getNewValue(),
                    history.getNewColor(),
                    makeLink(clazz, history.getNewId()))
            );
        }

        if (En_HistoryAction.CHANGE.equals(history.getAction())) {
            historyItem.oldValueContainer().add(makeItem(
                    history.getId(),
                    history.getAction(),
                    history.getType(),
                    history.getOldValue(),
                    history.getOldColor(),
                    makeLink(clazz, history.getOldId())
            ));

            historyItem.newValueContainer().add(makeItem(
                    history.getId(),
                    history.getAction(),
                    history.getType(),
                    history.getNewValue(),
                    history.getNewColor(),
                    makeLink(clazz, history.getNewId())
            ));
        }

        if (En_HistoryAction.REMOVE.equals(history.getAction())) {
            historyItem.removedValueContainer().add(makeItem(
                    history.getId(),
                    history.getAction(),
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

    private static Widget makeItem(Long historyId,
                                   En_HistoryAction historyAction,
                                   En_HistoryType historyType,
                                   String value,
                                   String color,
                                   String link) {
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

        if (En_HistoryType.CASE_NAME.equals(historyType)) {
            CaseHistorySimpleItemView caseHistorySimpleItemView = caseHistorySimpleItemViewProvider.get();
            caseHistorySimpleItemView.setName(value);

            return caseHistorySimpleItemView;
        }

        if (En_HistoryType.CASE_INFO.equals(historyType)) {
            CaseHistoryLinkItemView caseHistorySimpleItemView = caseHistoryLinkItemViewProvider.get();
            caseHistorySimpleItemView.setLink(getCaseInfoHistoryLabel(historyAction), "#", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    event.preventDefault();
                    if (activity != null){
                        activity.fireEvent(new CommentAndHistoryEvents.ShowCaseInfoChanges(historyId));
                    }
                }
            });

            return caseHistorySimpleItemView;
        }

        if (En_HistoryType.CASE_PAUSE_DATE.equals(historyType)
                || En_HistoryType.CASE_DEADLINE.equals(historyType)) {
            CaseHistorySimpleItemView caseHistorySimpleItemView = caseHistorySimpleItemViewProvider.get();
            caseHistorySimpleItemView.setName(makeDateString(value, historyType));

            return caseHistorySimpleItemView;
        }

        if (En_HistoryType.CASE_WORK_TRIGGER.equals(historyType)) {
            CaseHistorySimpleItemView caseHistorySimpleItemView = caseHistorySimpleItemViewProvider.get();
            caseHistorySimpleItemView.setName(workTriggerLang.getName(En_WorkTrigger.valueOf(value)));

            return caseHistorySimpleItemView;
        }

        if (En_HistoryType.CASE_MANAGER.equals(historyType)
                || En_HistoryType.PLAN.equals(historyType)
                || En_HistoryType.CASE_PRODUCT.equals(historyType)
                || En_HistoryType.CASE_PLATFORM.equals(historyType)
                || En_HistoryType.CASE_INITIATOR.equals(historyType)
                || En_HistoryType.CASE_MANAGER_COMPANY.equals(historyType)
                || En_HistoryType.CASE_INITIATOR_COMPANY.equals(historyType)
                || En_HistoryType.CASE_LINK.equals(historyType)
                || En_HistoryType.CASE_ATTACHMENT.equals(historyType)
        ) {
            CaseHistoryLinkItemView caseHistoryLinkItemView = caseHistoryLinkItemViewProvider.get();
            caseHistoryLinkItemView.setLink(transliteration(value), link);

            return caseHistoryLinkItemView;
        }

        if (En_HistoryType.DEPARTURE_DATE.equals(historyType)) {
            CaseHistoryLinkItemView caseHistoryDateItemView = caseHistoryLinkItemViewProvider.get();
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
            CaseHistoryLinkItemView caseHistoryDateItemView = caseHistoryLinkItemViewProvider.get();
            caseHistoryDateItemView.setLink(value, null);

            return caseHistoryDateItemView;
        }

        if (En_HistoryType.CARD_STATE.equals(historyType)) {
            CaseHistoryStateItemView caseHistoryStateItemView = caseHistoryStateItemViewProvider.get();
            caseHistoryStateItemView.setName(cardStateLang.getStateName(new CaseState(value)));
            caseHistoryStateItemView.setColor(color);

            return caseHistoryStateItemView;
        }

        if (En_HistoryType.CARD_MANAGER.equals(historyType)) {
            CaseHistoryLinkItemView caseHistoryLinkItemView = caseHistoryLinkItemViewProvider.get();
            caseHistoryLinkItemView.setLink(transliteration(value), link);

            return caseHistoryLinkItemView;
        }

        if (En_HistoryType.CARD_BATCH_STATE.equals(historyType)) {
            CaseHistoryStateItemView caseHistoryStateItemView = caseHistoryStateItemViewProvider.get();
            caseHistoryStateItemView.setName(cardBatchStateLang.getStateName(new CaseState(value)));
            caseHistoryStateItemView.setColor(color);

            return caseHistoryStateItemView;
        }

        if (En_HistoryType.CARD_BATCH_IMPORTANCE.equals(historyType)) {
            CaseHistoryImportanceItemView caseHistoryImportanceItemView = caseHistoryImportanceItemViewProvider.get();
            caseHistoryImportanceItemView.setName(value);
            caseHistoryImportanceItemView.setColor(color);

            return caseHistoryImportanceItemView;
        }

        return null;
    }

    private static String getCaseInfoHistoryLabel(En_HistoryAction historyAction) {
        if (historyAction == null) return lang.valueNotSet();
        switch (historyAction) {
            case ADD:
                return lang.historyResultOfAdding();
            case CHANGE:
                return lang.historyModificationResult();
            case REMOVE:
                return lang.historyDeletionResult();
            default:
                return lang.valueNotSet();
        }

    }

    private static boolean isCaseInfoType(En_HistoryType historyType){
        return En_HistoryType.CASE_INFO.equals(historyType);
    }

    private static String makeDateString(String value, En_HistoryType historyType) {
        Long millis = NumberUtils.parseLong(value);
        if (millis == null){
            log.warning("Parse history date error: value = " + value + ", type = " + historyType);
            return lang.error();
        }
        return DateFormatter.formatDateOnly(new Date(millis));
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
    private static Provider<CaseHistoryLinkItemView> caseHistoryLinkItemViewProvider;
    @Inject
    private static Provider<CaseHistorySimpleItemView> caseHistorySimpleItemViewProvider;
    @Inject
    private static CaseLinkProvider caseLinkProvider;
    @Inject
    private static DeliveryStateLang deliveryStateLang;
    @Inject
    private static ModuleStateLang moduleStateLang;
    @Inject
    private static CardStateLang cardStateLang;
    @Inject
    private static CardBatchStateLang cardBatchStateLang;
    @Inject
    private static En_BundleTypeLang bundleTypeLang;
    @Inject
    private static Provider<CaseHistoryItemsContainer> caseHistoryItemsContainerProvider;
    @Inject
    private static En_WorkTriggerLang workTriggerLang;

    private static final Logger log = Logger.getLogger(CommentOrHistoryUtils.class.getName());
}
