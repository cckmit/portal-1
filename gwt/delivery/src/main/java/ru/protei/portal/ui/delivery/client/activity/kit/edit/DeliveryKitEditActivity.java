package ru.protei.portal.ui.delivery.client.activity.kit.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CommentsAndHistories;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.KitEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseCommentControllerAsync;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils;
import ru.protei.portal.ui.common.client.view.casecomment.list.CommentAndHistoryListView;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItemsContainer;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.XL_MODAL;
import static ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils.getSortedCommentOrHistoryList;
import static ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils.transliteration;

public abstract class DeliveryKitEditActivity implements Activity, AbstractDeliveryKitEditActivity,
        AbstractDialogDetailsActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        prepareDialog(dialogView);
    }

    @Event
    public void onShow(KitEvents.Edit event) {

        if (!hasViewAccess()) {
            fireEvent(new NotifyEvents.Show(lang.errAccessDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        view.setStateEnabled(hasEditAccess());
        view.setNameEnabled(hasEditAccess());
        dialogView.saveButtonEnabled().setEnabled(hasEditAccess());
        requestKit(event.kitId);
    }

    @Override
    public void onSaveClicked() {
        if (kit.getStateId() == null) {
            fireEvent(new NotifyEvents.Show(lang.deliveryValidationInvalidKits(), NotifyEvents.NotifyType.ERROR));
            return;
        }
        Kit kit = fillDto();
        save(kit);
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private void requestKit(Long kitId) {
        deliveryController.getKit(kitId, new FluentCallback<Kit>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(kit -> {
                    this.kit = kit;
                    fillView(kit);
                    dialogView.showPopup();
                }));
    }

    private void prepareDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(this);
        dialog.addStyleName(XL_MODAL);
        dialog.getBodyContainer().add(view.asWidget());
        dialog.removeButtonVisibility().setVisible(false);
        dialog.setCloseVisible(false);
    }

    private boolean hasEditAccess() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    private boolean hasViewAccess() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW);
    }

    private Kit fillDto() {
        kit.setName(view.name().getValue());
        kit.setState(view.state().getValue());
        return kit;
    }

    private void save(final Kit kit) {
        dialogView.saveButtonEnabled().setEnabled(false);
        deliveryController.updateKit(kit, new FluentCallback<Void>()
                .withError(throwable -> {
                    dialogView.saveButtonEnabled().setEnabled(true);
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(list -> {
                    dialogView.saveButtonEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new KitEvents.Changed(kit.getDeliveryId()));
                    dialogView.hidePopup();
                }));
    }

    private void fillView(Kit kit) {

        view.setSerialNumber(kit.getSerialNumber());
        view.state().setValue(kit.getState());
        view.name().setValue(kit.getName());
        view.setCreatedBy(lang.createBy(kit.getCreator() == null ? "" : transliteration(kit.getCreator().getDisplayShortName()),
                DateFormatter.formatDateTime(kit.getCreated())));

        fillHistory();
    }

    private void fillHistory(){
        view.getItemsContainer().clear();
        view.getItemsContainer().add(commentAndHistoryView.asWidget());
        commentAndHistoryView.clearItemsContainer();

        caseCommentController.getCommentsAndHistories(En_CaseType.KIT, kit.getId(), new FluentCallback<CommentsAndHistories>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(commentsAndHistories -> {
                    fillHistoryView(commentsAndHistories);
                })
        );
    }

    public void fillHistoryView(CommentsAndHistories commentsAndHistories) {

        List<CommentsAndHistories.CommentOrHistory> commentOrHistoryList
                = getSortedCommentOrHistoryList(commentsAndHistories.getCommentOrHistoryList());

        List<History> histories = stream(commentOrHistoryList)
                .filter(o -> o.getHistory() != null)
                .map(o -> o.getHistory())
                .collect(Collectors.toList());

        List<CaseHistoryItemsContainer> caseHistoryItemsContainers = CommentOrHistoryUtils.fillView(
                histories, commentAndHistoryView.commentsAndHistoriesContainer());

        historyItemsContainers.addAll(caseHistoryItemsContainers);

        commentAndHistoryView.setNewCommentHidden(true);
        historyItemsContainers.forEach(historyItemsContainer -> historyItemsContainer.setVisible(true));
    }

    @Inject
    private AbstractDeliveryKitEditView view;
    @Inject
    PolicyService policyService;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;
    @Inject
    private DeliveryControllerAsync deliveryController;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    CaseCommentControllerAsync caseCommentController;
    @Inject
    Lang lang;
    @Inject
    CommentAndHistoryListView commentAndHistoryView;

    private Kit kit;
    private final List<CaseHistoryItemsContainer> historyItemsContainers = new LinkedList<>();
}
