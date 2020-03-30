package ru.protei.portal.ui.ipreservation.client.activity.subnet.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

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
        event.parent.clear();
        event.parent.add(view.asWidget());

        this.subnet = event.subnet;

        fillView();
    }

    @Override
    public void onSaveClicked() {
        if (isNew() && !policyService.hasPrivilegeFor( En_Privilege.SUBNET_CREATE) ) {
            return;
        }

        if (!isNew() && !policyService.hasPrivilegeFor( En_Privilege.SUBNET_EDIT ) ) {
            return;
        }

        if (!validateView()) {
            return;
        }

        fillSubnet();

        view.saveEnabled().setEnabled(false);

        ipReservationService.saveSubnet(subnet, new FluentCallback<Subnet>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aVoid -> {
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

    private boolean isNew() {
        return subnet == null || subnet.getId() == null;
    }

/*    private void requestSubnet(Long subnetId, Consumer<Subnet> successAction) {
        ipReservationService.getSubnet( subnetId, new RequestCallback<Subnet>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Subnet subnet) {
                SubnetEditActivity.this.subnet = subnet;
                successAction.accept(subnet);
            }
        });
    }*/

    private void resetView () {
        view.address().setValue("");
        view.mask().setValue("");
        view.comment().setText("");

        view.saveVisibility().setVisible( hasPrivileges(subnet == null ? null : subnet.getId()) );
        view.saveEnabled().setEnabled(true);
    }

    private void fillView() {
        if (subnet == null) {
            subnet = new Subnet();
        }
        view.address().setValue(subnet.getAddress());
        //view.mask().setValue(subnet.getMask());
        view.mask().setValue("0/24");
        view.comment().setText(subnet.getComment());

        view.addressEnabled().setEnabled(subnet.getId() == null);
        view.maskEnabled().setEnabled(false);
    }

    private Subnet fillSubnet() {
        subnet.setAddress(view.address().getValue());
        subnet.setMask(view.mask().getValue());
        subnet.setComment(view.comment().getText());
        return subnet;
    }

    private boolean validateView() {
        if(!view.addressValidator().isValid()){
            fireEvent(new NotifyEvents.Show(lang.reservedIpWrongSubnetAddress(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(!view.maskValidator().isValid()){
            fireEvent(new NotifyEvents.Show(lang.reservedIpWrongSubnetMask(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private boolean hasPrivileges(Long subnetId) {
        if (isNew() && policyService.hasPrivilegeFor(En_Privilege.SUBNET_CREATE)) {
            return true;
        }

        if (!isNew() && policyService.hasPrivilegeFor(En_Privilege.SUBNET_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractSubnetEditView view;
    @Inject
    IpReservationControllerAsync ipReservationService;
    @Inject
    PolicyService policyService;

    private Subnet subnet;
}