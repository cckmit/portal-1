package ru.protei.portal.ui.delivery.client.activity.pcborder.edit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PcbOrderControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.delivery.client.activity.pcborder.common.AbstractPcbOrderCommonInfoEditActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.common.AbstractPcbOrderCommonInfoEditView;
import ru.protei.portal.ui.delivery.client.activity.pcborder.edit.modal.AbstractPcbOrderModalEditActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.edit.modal.AbstractPcbOrderModalEditView;
import ru.protei.portal.ui.delivery.client.activity.pcborder.meta.AbstractPcbOrderMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.meta.AbstractPcbOrderMetaView;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.ERROR;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.SUCCESS;
import static ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils.transliteration;

public abstract class PcbOrderEditActivity implements Activity, AbstractPcbOrderEditActivity,
        AbstractPcbOrderCommonInfoEditActivity, AbstractPcbOrderMetaActivity,
        AbstractPcbOrderModalEditActivity, AbstractDialogDetailsActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);

        metaView.setActivity(this);
        view.getMetaContainer().add(metaView);

        commonInfoEditView.setActivity(this);
        commonInfoEditView.buttonsContainerVisibility().setVisible(true);
        view.getCommonInfoEditContainer().add(commonInfoEditView);

        modalView.setActivity(this);
        dialogDetailsView.setActivity(this);
        dialogDetailsView.getBodyContainer().add(modalView.asWidget());
        dialogDetailsView.removeButtonVisibility().setVisible(false);
        dialogDetailsView.setHeader(lang.pcbOrderModalAmountReceived());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        this.authProfile = event.profile;
    }

    @Event
    public void onShow(PcbOrderEvents.ShowPreview event) {
        HasWidgets container = event.parent;
        if (!hasViewPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        viewModeIsPreview(true);
        requestPcbOrder(event.id, container);
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(PcbOrderEvents.Edit event) {
        HasWidgets container = initDetails.parent;
        if (!hasEditPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        Window.scrollTo(0, 0);
        viewModeIsPreview(false);
        requestPcbOrder(event.id, container);
    }

    @Override
    public void onAmountChanged() {
        validateAmount();
    }

    @Override
    public void onReceivedAmountChanged() {
        validateReceivedAmount();
    }

    @Override
    public void onStateChanged() {
        En_PcbOrderState state = metaView.state().getValue();
        if (Objects.equals(state, pcbOrder.getState())) {
            return;
        }
        if (En_PcbOrderState.RECEIVED.equals(metaView.state().getValue())) {
            modalView.receivedAmount().setValue(null);
            dialogDetailsView.showPopup();
        } else {
            pcbOrder.setState(metaView.state().getValue());
            onMetaStateChanged();
        }
    }

    @Override
    public void onPromptnessChanged() {
        En_PcbOrderPromptness promptness = metaView.promptness().getValue();
        if (Objects.equals(promptness, pcbOrder.getPromptness())) {
            return;
        }
        pcbOrder.setPromptness(promptness);
        onMetaChanged();
    }

    @Override
    public void onOrderTypeChanged() {
        En_PcbOrderType orderType = metaView.orderType().getValue();
        if (!En_PcbOrderType.STENCIL.equals(orderType)) {
            metaView.stencilTypeVisibility().setVisible(false);
        }
        if (Objects.equals(orderType, pcbOrder.getType())) {
            return;
        }
        if (En_PcbOrderType.STENCIL.equals(orderType)) {
            metaView.stencilTypeVisibility().setVisible(true);
            return;
        }
        pcbOrder.setType(orderType);
        pcbOrder.setStencilType(null);
        onMetaChanged();
    }

    @Override
    public void onStencilTypeChanged() {
        En_StencilType stencilType = metaView.stencilType().getValue();
        if (stencilType == null
                || Objects.equals(stencilType, pcbOrder.getStencilType())) {
            return;
        }
        pcbOrder.setType(metaView.orderType().getValue());
        pcbOrder.setStencilType(stencilType);
        onMetaChanged();
    }

    @Override
    public void onContractorChanged() {
        Long companyId = metaView.contractor().getValue().getId();
        if (Objects.equals(companyId, pcbOrder.getCompanyId())) {
            return;
        }
        pcbOrder.setCompanyId(companyId);
        onMetaChanged();
    }

    @Override
    public void onOrderDateChanged() {
        Date orderDate = metaView.orderDate().getValue();
        if (isDateEquals(orderDate, pcbOrder.getOrderDate()) || !metaView.isOrderDateValid()) {
            return;
        }
        pcbOrder.setOrderDate(orderDate);
        onMetaChanged();
    }

    @Override
    public void onReadyDateChanged() {
        Date readyDate = metaView.readyDate().getValue();
        if (isDateEquals(readyDate, pcbOrder.getReadyDate()) || !metaView.isReadyDateValid()) {
            return;
        }
        pcbOrder.setReadyDate(readyDate);
        onMetaChanged();
    }

    @Override
    public void onReceiptDateChanged() {
        Date receiptDate = metaView.receiptDate().getValue();
        if (isDateEquals(receiptDate, pcbOrder.getReceiptDate()) || !metaView.isReceiptDateValid()) {
            return;
        }
        pcbOrder.setReceiptDate(receiptDate);
        onMetaChanged();
    }

    @Override
    public void onCommonInfoEditClicked() {
        commonInfoEditView.cardType().setValue(new EntityOption(pcbOrder.getCardTypeName(), pcbOrder.getCardTypeId()));
        commonInfoEditView.amount().setValue(pcbOrder.getAmount());
        commonInfoEditView.modification().setValue(pcbOrder.getModification());
        commonInfoEditView.comment().setValue(pcbOrder.getComment());
        switchCommonInfoToEdit(true);
    }

    @Override
    public void onSaveCommonInfoClicked() {
        String error = getCommonValidationError();
        if (error != null) {
            showError(error);
            return;
        }
        PcbOrder pcbOrder = fillCommonInfo();
        saveCommonInfo(pcbOrder);
    }

    @Override
    public void onCancelSaveCommonInfoClicked() {
        switchCommonInfoToEdit(false);
    }

    @Override
    public void onBackClicked() {
        fireEvent(new PcbOrderEvents.Show(!isNew(pcbOrder)));
    }

    @Override
    public void onSaveClicked() {
        if (!validateReceivedAmount()) {
            showError(lang.pcbOrderAmountValidationError());
            return;
        }

        if (modalView.receivedAmount().getValue() >= pcbOrder.getAmount()) {
            pcbOrder.setState(metaView.state().getValue());
            onMetaStateChanged();
        } else {
            onPartiallyCompletedPcbOrder(modalView.receivedAmount().getValue());
        }
    }

    @Override
    public void onCancelClicked() {
        metaView.state().setValue(pcbOrder.getState());
        dialogDetailsView.hidePopup();
    }

    private void fillView(PcbOrder pcbOrder) {
        this.pcbOrder = pcbOrder;
        if (pcbOrder == null) return;

        view.setCreatedBy(lang.createBy(pcbOrder.getCreatorShortName() == null ? "" : transliteration(pcbOrder.getCreatorShortName()),
                DateFormatter.formatDateTime(pcbOrder.getCreated())));
        view.setCardTypeRO(pcbOrder.getCardTypeName());
        view.setAmountRO(String.valueOf(pcbOrder.getAmount()));
        view.setModificationRO(pcbOrder.getModification());
        view.setCommentRO(pcbOrder.getComment());

        metaView.state().setValue(pcbOrder.getState());
        metaView.promptness().setValue(pcbOrder.getPromptness());
        metaView.orderType().setValue(pcbOrder.getType());
        fillStencilType(pcbOrder.getType(), pcbOrder.getStencilType());
        metaView.contractor().setValue(pcbOrder.getCompanyId() == null ? null :
                new EntityOption(pcbOrder.getCompanyName(), pcbOrder.getCompanyId()));
        metaView.orderDate().setValue(pcbOrder.getOrderDate());
        metaView.readyDate().setValue(pcbOrder.getReadyDate());
        metaView.receiptDate().setValue(pcbOrder.getReceiptDate());
        metaView.clearDatesValidationMarks();
    }

    private void fillStencilType(En_PcbOrderType orderType, En_StencilType stencilType) {
        if (En_PcbOrderType.STENCIL.equals(orderType)) {
            metaView.stencilTypeVisibility().setVisible(true);
            metaView.stencilType().setValue(stencilType);
        } else {
            metaView.stencilTypeVisibility().setVisible(false);
            metaView.stencilType().setValue(null);
        }
    }

    private void requestPcbOrder(Long pcbOrderId, HasWidgets container) {
        pcbOrderService.getPcbOrder(pcbOrderId, new FluentCallback<PcbOrder>()
                .withSuccess(pcbOrder -> {
                    fillView(pcbOrder);
                    attachToContainer(container);
                    switchCommonInfoToEdit(false);
                }));
    }

    private void viewModeIsPreview(boolean isPreviewMode) {
        view.backButtonVisibility().setVisible(!isPreviewMode);
        view.setPreviewStyles(isPreviewMode);
    }

    private void onMetaChanged() {
        pcbOrderService.updateMeta(pcbOrder, new FluentCallback<PcbOrder>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.pcbOrderSaved(), SUCCESS));
                    fireEvent(new PcbOrderEvents.Change(result.getId()));
                    fillView(result);
                }));
    }

    private void onMetaStateChanged() {
        pcbOrderService.updateMeta(pcbOrder, new FluentCallback<PcbOrder>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.pcbOrderSaved(), SUCCESS));
                    fireEvent(new PcbOrderEvents.ChangeState());
                    fillView(result);
                    dialogDetailsView.hidePopup();
                })
        );
    }

    private void onPartiallyCompletedPcbOrder(Integer receivedAmount) {
        pcbOrderService.updateMetaWithCreatingChildPbcOrder(pcbOrder, receivedAmount, new FluentCallback<PcbOrder>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.pcbOrderSaved(), SUCCESS));
                    fireEvent(new PcbOrderEvents.ChangeState());
                    fillView(result);
                    dialogDetailsView.hidePopup();
                })
        );
    }

    private PcbOrder fillCommonInfo() {
        pcbOrder.setCardTypeId(commonInfoEditView.cardType().getValue().getId());
        pcbOrder.setAmount(commonInfoEditView.amount().getValue());
        pcbOrder.setModification(commonInfoEditView.modification().getValue());
        pcbOrder.setComment(commonInfoEditView.comment().getValue());
        return pcbOrder;
    }

    private void saveCommonInfo(PcbOrder pcbOrder) {
        commonInfoEditView.saveEnabled().setEnabled(false);
        pcbOrderService.updateCommonInfo(pcbOrder, new FluentCallback<PcbOrder>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    commonInfoEditView.saveEnabled().setEnabled(true);
                })
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.pcbOrderSaved(), SUCCESS));
                    fireEvent(new PcbOrderEvents.Change(result.getId()));
                    commonInfoEditView.saveEnabled().setEnabled(true);
                    switchCommonInfoToEdit(false);
                    fillView(result);
                }));
    }

    private boolean isNew(PcbOrder pcbOrder) {
        return pcbOrder.getId() == null;
    }

    private void switchCommonInfoToEdit(boolean isEdit) {
        view.commonInfoEditButtonVisibility().setVisible(!isEdit);
        view.commonInfoEditContainerVisibility().setVisible(isEdit);
        view.commonInfoContainerVisibility().setVisible(!isEdit);
    }

    private String getCommonValidationError() {
        if (!validateAmount()) {
            return lang.pcbOrderAmountValidationError();
        }

        return null;
    }

    private boolean validateAmount() {
        Integer value = commonInfoEditView.amount().getValue();
        boolean isValid = value != null && value > 0;
        commonInfoEditView.setAmountValid(isValid);
        return isValid;
    }

    private boolean validateReceivedAmount() {
        Integer value = modalView.receivedAmount().getValue();
        boolean isValid = value != null && value > 0;
        modalView.setReceivedAmountValid(isValid);
        return isValid;
    }

    private boolean isDateEquals(Date dateField, Date dateMeta) {
        if (dateField == null) {
            return dateMeta == null;
        } else {
            return Objects.equals(dateField, dateMeta);
        }
    }

    private void attachToContainer(HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, ERROR));
    }

    private boolean hasViewPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_VIEW);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_EDIT);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractPcbOrderEditView view;
    @Inject
    AbstractPcbOrderCommonInfoEditView commonInfoEditView;
    @Inject
    AbstractPcbOrderMetaView metaView;
    @Inject
    AbstractPcbOrderModalEditView modalView;
    @Inject
    AbstractDialogDetailsView dialogDetailsView;
    @Inject
    private PcbOrderControllerAsync pcbOrderService;
    @Inject
    private PolicyService policyService;

    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    @ContextAware
    PcbOrder pcbOrder;

    private Profile authProfile;
    private AppEvents.InitDetails initDetails;
}
