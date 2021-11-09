package ru.protei.portal.ui.delivery.client.activity.pcborder.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_PcbOrderType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PcbOrderEvents;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PcbOrderControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.pcborder.common.AbstractPcbOrderCommonInfoEditActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.common.AbstractPcbOrderCommonInfoEditView;
import ru.protei.portal.ui.delivery.client.activity.pcborder.meta.AbstractPcbOrderMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.meta.AbstractPcbOrderMetaView;

import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.ERROR;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.SUCCESS;

public abstract class PcbOrderCreateActivity implements Activity, AbstractPcbOrderCreateActivity,
        AbstractPcbOrderCommonInfoEditActivity, AbstractPcbOrderMetaActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);

        metaView.setActivity(this);
        view.getMetaContainer().add(metaView);

        commonInfoEditView.setActivity(this);
        commonInfoEditView.buttonsContainerVisibility().setVisible(false);
        view.getCommonInfoContainer().add(commonInfoEditView);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(PcbOrderEvents.Create event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        prepare();
    }

    @Override
    public void onSaveClicked() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }
        PcbOrder pcbOrder = fillDto();
        save(pcbOrder);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onAmountChanged() {
        validateAmount();
    }

    @Override
    public void onOrderTypeChanged(En_PcbOrderType orderType) {
        if (En_PcbOrderType.STENCIL.equals(orderType)) {
            metaView.stencilTypeContainer().setVisible(true);
        } else {
            metaView.stencilTypeContainer().setVisible(false);
            metaView.stencilType().setValue(null);
        }
    }

    private void prepare() {
        commonInfoEditView.cardType().setValue(null);
        commonInfoEditView.amount().setValue(null);
        commonInfoEditView.modification().setValue(null);
        commonInfoEditView.comment().setValue(null);
        metaView.state().setValue(null);
        metaView.promptness().setValue(null);
        metaView.orderType().setValue(null);
        metaView.stencilTypeContainer().setVisible(false);
        metaView.stencilType().setValue(null);
        metaView.contractor().setValue(null);
        metaView.orderDate().setValue(null);
        metaView.setOrderDateValid(true);
        metaView.readyDate().setValue(null);
        metaView.setReadyDateValid(true);
        metaView.receiptDate().setValue(null);
        metaView.setReceiptDateValid(true);
    }

    private PcbOrder fillDto() {
        PcbOrder pcbOrder = new PcbOrder();
        pcbOrder.setCardTypeId(commonInfoEditView.cardType().getValue().getId());
        pcbOrder.setAmount(commonInfoEditView.amount().getValue());
        pcbOrder.setModification(commonInfoEditView.modification().getValue());
        pcbOrder.setComment(commonInfoEditView.comment().getValue());
        pcbOrder.setState(metaView.state().getValue());
        pcbOrder.setPromptness(metaView.promptness().getValue());
        pcbOrder.setType(metaView.orderType().getValue());
        pcbOrder.setStencilType(metaView.stencilType().getValue());
        pcbOrder.setCompanyId(metaView.contractor().getValue().getId());
        pcbOrder.setOrderDate(metaView.orderDate().getValue());
        pcbOrder.setReadyDate(metaView.readyDate().getValue());
        pcbOrder.setReceiptDate(metaView.receiptDate().getValue());
        return pcbOrder;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, ERROR));
    }

    private String getValidationError() {
        if (commonInfoEditView.cardType().getValue() == null) {
            return lang.pcbOrderCardTypeValidationError();
        }
        if (!validateAmount()) {
            return lang.pcbOrderAmountValidationError();
        }
        if (metaView.state().getValue() == null) {
            return lang.pcbOrderStateValidationError();
        }
        if (metaView.promptness().getValue() == null) {
            return lang.pcbOrderPromptnessValidationError();
        }
        if (metaView.orderType().getValue() == null) {
            return lang.pcbOrderOrderTypeValidationError();
        }
        if (metaView.stencilType().getValue() == null
                && En_PcbOrderType.STENCIL.equals(metaView.orderType().getValue())) {
            return lang.pcbOrderStencilTypeValidationError();
        }
        if (metaView.contractor().getValue() == null) {
            return lang.pcbOrderContractorValidationError();
        }
        return null;
    }

    private void save(PcbOrder pcbOrder) {
        view.saveEnabled().setEnabled(false);
        pcbOrderService.savePcbOrder(pcbOrder, new FluentCallback<PcbOrder>()
            .withError(throwable -> {
                view.saveEnabled().setEnabled(true);
                defaultErrorHandler.accept(throwable);
            })
            .withSuccess(result -> {
                view.saveEnabled().setEnabled(true);
                fireEvent(new NotifyEvents.Show(lang.pcbOrderCreated(), SUCCESS));
                fireEvent(new Back());
            }));
    }

    private boolean validateAmount() {
        Integer value = commonInfoEditView.amount().getValue();
        boolean isValid = value != null && value > 0;
        commonInfoEditView.setAmountValid(isValid);
        return isValid;
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_CREATE);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractPcbOrderCreateView view;
    @Inject
    AbstractPcbOrderCommonInfoEditView commonInfoEditView;
    @Inject
    AbstractPcbOrderMetaView metaView;
    @Inject
    private PcbOrderControllerAsync pcbOrderService;
    @Inject
    private PolicyService policyService;
    @Inject
    En_PcbOrderStateLang pcbOrderStateLang;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private AppEvents.InitDetails initDetails;
}
