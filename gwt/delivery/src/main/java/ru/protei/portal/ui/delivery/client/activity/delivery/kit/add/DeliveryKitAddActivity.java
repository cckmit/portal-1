package ru.protei.portal.ui.delivery.client.activity.delivery.kit.add;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.widget.kit.list.AbstractDeliveryKitListActivity;
import ru.protei.portal.ui.delivery.client.widget.kit.list.DeliveryKitList;

import java.util.List;
import java.util.function.Consumer;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.XL_MODAL;

public abstract class DeliveryKitAddActivity implements Activity, AbstractDeliveryKitAddActivity,
        AbstractDeliveryKitListActivity, AbstractDialogDetailsActivity {

    @Inject
    public void onInit() {
        kitList.setActivity(this);
        kitList.setAddMode(true);
        prepareDialog(dialogView);
    }

    @Event
    public void onShow(KitEvents.Add event) {

        if (!hasCreatePrivileges()) {
            fireEvent(new NotifyEvents.Show(lang.errAccessDenied(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        this.deliveryId = event.deliveryId;
        this.backHandler = event.backHandler;

        kitList.clear();
        kitList.updateAllowChangingState(false);
        kitList.updateSerialNumbering(true);

        getDeliveryStateId(deliveryId, stateId -> kitList.updateAllowChangingState(stateId != CrmConstants.State.PRELIMINARY));

        dialogView.showPopup();
    }

    @Override
    public void getLastSerialNumber(Consumer<String> success) {
        deliveryController.getLastSerialNumber(deliveryId, new FluentCallback<String>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    @Override
    public void getCaseState(Long id, Consumer<CaseState> success) {
        caseStateController.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    @Override
    public void onSaveClicked() {
        if (!kitList.isValid()) {
            fireEvent(new NotifyEvents.Show(lang.deliveryValidationInvalidKits(), NotifyEvents.NotifyType.ERROR));
            return;
        }
        create(kitList.getValue());
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private void getDeliveryStateId(Long deliveryId, Consumer<Long> success) {
        deliveryController.getDeliveryStateId(deliveryId, new FluentCallback<Long>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    private void prepareDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(this);
        dialog.addStyleName(XL_MODAL);
        dialog.getBodyContainer().clear();
        dialog.getBodyContainer().add(kitList.asWidget());
        dialog.setHeader(lang.deliveryKitsAddHeader());
        dialog.removeButtonVisibility().setVisible(false);
    }

    private boolean hasCreatePrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
    }

    private void create(List<Kit> kits) {
        dialogView.saveButtonEnabled().setEnabled(false);
        deliveryController.addKits(kits, deliveryId, new FluentCallback<List<Kit>>()
                .withError(throwable -> {
                    dialogView.saveButtonEnabled().setEnabled(true);
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(list -> {
                    dialogView.saveButtonEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    dialogView.hidePopup();
                    if (backHandler != null) {
                        backHandler.accept(list);
                    }
                }));
    }

    @Inject
    private DeliveryKitList kitList;
    @Inject
    PolicyService policyService;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;
    @Inject
    private CaseStateControllerAsync caseStateController;
    @Inject
    private DeliveryControllerAsync deliveryController;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    Lang lang;

    private Long deliveryId;
    private Consumer<List<Kit>> backHandler;
}
