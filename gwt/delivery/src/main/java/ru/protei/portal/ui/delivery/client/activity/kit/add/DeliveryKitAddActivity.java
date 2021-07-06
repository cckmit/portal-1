package ru.protei.portal.ui.delivery.client.activity.kit.add;

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
        view.setActivity(this);
        kitList.setActivity(this);
        kitList.setAddMode(true);
        view.getKitsContainer().clear();
        view.getKitsContainer().add(kitList.asWidget());
        prepareDialog(dialogView);
    }

    @Event
    public void onShow(KitEvents.Add event) {

        if (!hasEditPrivileges()) {
            return;
        }

        this.deliveryId = event.deliveryId;

        kitList.clear();
        kitList.setAllowChangingState(event.stateId != CrmConstants.State.PRELIMINARY);
        kitList.updateSerialNumbering(true);

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

    private void prepareDialog(AbstractDialogDetailsView dialog) {
        dialog.setActivity(this);
        dialog.addStyleName(XL_MODAL);
        dialog.getBodyContainer().add(view.asWidget());
        dialog.setHeader(lang.deliveryKitsAddHeader());
        dialog.removeButtonVisibility().setVisible(false);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
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
                    fireEvent(new KitEvents.Added(list, deliveryId));
                    dialogView.hidePopup();
                }));
    }

    @Inject
    private AbstractDeliveryKitAddView view;
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
}
