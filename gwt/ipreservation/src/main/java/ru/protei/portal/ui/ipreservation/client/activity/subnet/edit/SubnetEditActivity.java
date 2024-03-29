package ru.protei.portal.ui.ipreservation.client.activity.subnet.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

/**
 * Активность карточки создания и редактирования подсети
 */
public abstract class SubnetEditActivity implements AbstractSubnetEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow (IpReservationEvents.EditSubnet event) {
        if (!hasPrivileges()) {
            fireEvent(new IpReservationEvents.CloseEdit());
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

        this.subnet = event.subnet;

        fillView();
    }

    @Override
    public void onSaveClicked() {
        if (!hasPrivileges() || !validateView()) {
            return;
        }

        view.saveEnabled().setEnabled(false);

        ipReservationService.saveSubnet(fillSubnet(), new FluentCallback<Subnet>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    showErrorFromServer(throwable);
                })
                .withSuccess(subnet -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.ChangedSubnet(subnet, true));
                    fireEvent(new IpReservationEvents.CloseEdit());
                    fireEvent(new IpReservationEvents.ChangeModel());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new IpReservationEvents.CloseEdit());
    }

    @Override
    public void onChangeAddress() {

        if (!view.addressValidator().isValid()) {
            view.setAddressStatus(NameStatus.NONE);
            return;
        }

        ipReservationService.isSubnetAddressExists(
                view.address().getValue().trim(),
                subnet.getId(),
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) { }

                    @Override
                    public void onSuccess(Boolean isExists) {
                        view.setAddressStatus(isExists ? NameStatus.ERROR : NameStatus.SUCCESS);
                    }
                }
        );
    }

    private boolean isNew() {
        return subnet == null || subnet.getId() == null;
    }

    private void fillView() {
        if (subnet == null) {
            subnet = new Subnet();
        }
        view.address().setValue(subnet.getAddress());
        view.mask().setValue(CrmConstants.IpReservation.SUBNET_MASK);
        view.allowForReserve().setValue(subnet.isAllowForReserve());
        view.comment().setText(subnet.getComment());

        view.addressEnabled().setEnabled(subnet.getId() == null);
        view.maskEnabled().setEnabled(false);

        resetValidationStatus();
    }

    private Subnet fillSubnet() {
        subnet.setAddress(view.address().getValue());
        subnet.setMask(view.mask().getValue());
        subnet.setAllowForReserve(view.allowForReserve().getValue());
        subnet.setComment(view.comment().getText());
        return subnet;
    }

    private boolean validateView() {
        if(view.address().getValue() == null || !view.addressValidator().isValid()){
            fireEvent(new NotifyEvents.Show(lang.reservedIpWrongSubnetAddress(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private void resetValidationStatus() {
        view.setAddressStatus(NameStatus.NONE);
    }

    private boolean hasPrivileges() {
        if (isNew() && policyService.hasPrivilegeFor(En_Privilege.SUBNET_CREATE)) {
            return true;
        }

        if (!isNew() && policyService.hasPrivilegeFor(En_Privilege.SUBNET_EDIT)) {
            return true;
        }

        return false;
    }

    private void showErrorFromServer(Throwable throwable) {
        errorHandler.accept(throwable);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractSubnetEditView view;
    @Inject
    IpReservationControllerAsync ipReservationService;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler errorHandler;

    private Subnet subnet;
}