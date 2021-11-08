package ru.protei.portal.ui.delivery.client.activity.pcborder.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
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
//        commonInfoEditView.typeEnabled().setEnabled(true);
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
//
//    @Override
//    public void onCardTypeChanged(Long cardTypeId) {
//
//    }

//    @Override
//    public void onDeadlineChanged() {
//        validateDeadline();
//    }

    @Override
    public void onAmountChanged() {
        validateAmount();
    }

    private void prepare() {
//        commonInfoEditView.type().setValue(null);
//        commonInfoEditView.number().setValue(null);
//        commonInfoEditView.article().setValue(null);
//        commonInfoEditView.amount().setValue(null);
//        commonInfoEditView.params().setValue(null);
//        commonInfoEditView.contractors().setValue(null);
//        commonInfoEditView.hidePrevCardBatchInfo();
//        metaView.deadline().setValue(null);
//        metaView.setDeadlineValid(true);
//        metaView.stateEnable().setEnabled(false);
    }

    private PcbOrder fillDto() {
        PcbOrder pcbOrder = new PcbOrder();
//        cardBatch.setTypeId(commonInfoEditView.type().getValue().getId());
        return pcbOrder;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, ERROR));
    }

    private String getValidationError() {
//        if (null == commonInfoEditView.type().getValue()) {
//            return lang.cardBatchTypeValidationError();
//        }
//
//        if (isEmpty(commonInfoEditView.number().getValue()) || !commonInfoEditView.isNumberValid()) {
//            return lang.cardBatchNumberValidationError();
//        }
//
//        if (isEmpty(commonInfoEditView.article().getValue()) || !commonInfoEditView.isArticleValid()) {
//            return lang.cardBatchArticleValidationError();
//        }
//
//        if (CollectionUtils.isEmpty(commonInfoEditView.contractors().getValue())) {
//            return lang.cardBatchContractorsValidationError();
//        }
//
//        if (!validateAmount()) {
//            return lang.cardBatchAmountValidationError();
//        }
//
//        if (!validateDeadline()) {
//            return lang.cardBatchDeadlineValidationError();
//        }

        return null;
    }

    private void save(PcbOrder pcbOrder) {
        view.saveEnabled().setEnabled(false);
        pcbOrderService.savePcbOrder(pcbOrder, new FluentCallback<PcbOrder>()
            .withError(throwable -> {
                view.saveEnabled().setEnabled(true);
                defaultErrorHandler.accept(throwable);
            })
            .withSuccess(id -> {
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
