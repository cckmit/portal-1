package ru.protei.portal.ui.ipreservation.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
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
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.function.Consumer;

/**
 * Активность карточки создания и редактирования подсети
 */
public abstract class SubnetEditActivity implements AbstractSubnetEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow (IpReservationEvents.EditSubnet event) {
        if (!hasPrivileges(event.subnetId)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        if (event.subnetId == null) {
            subnet = new Subnet();
            resetView();
        } else {
            resetView();
            requestSubnet(event.subnetId, this::fillView);
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        fillSubnet(subnet);

        view.saveEnabled().setEnabled(false);

        ipReservationService.saveSubnet(subnet, new FluentCallback<Subnet>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aVoid -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private boolean isNew(Subnet subnet) {
        return subnet.getId() == null;
    }

    private void requestSubnet(Long subnetId, Consumer<Subnet> successAction) {
        ipReservationService.getSubnet( subnetId, new RequestCallback<Subnet>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Subnet subnet) {
                SubnetEditActivity.this.subnet = subnet;
                successAction.accept(subnet);
            }
        });
    }

    private void resetView () {
        view.address().setValue("");
        view.mask().setValue("");
        view.comment().setText("");
        view.local().setValue(true);

        view.saveVisibility().setVisible( hasPrivileges(subnet == null ? null : subnet.getId()) );
        view.saveEnabled().setEnabled(true);
    }

    private void fillView(Subnet subnet) {
        view.address().setValue(subnet.getAddress());
        view.mask().setValue(subnet.getMask());
        view.comment().setText(subnet.getComment());
        view.local().setValue(subnet.isLocal());
    }

    private Subnet fillSubnet(Subnet subnet) {
        subnet.setAddress(view.address().getValue());
        subnet.setMask(view.mask().getValue());
        subnet.setComment(view.comment().getText());
        subnet.setLocal(view.local().getValue());
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
        if (subnetId == null && policyService.hasPrivilegeFor(En_Privilege.SUBNET_CREATE)) {
            return true;
        }

        if (subnetId != null && policyService.hasPrivilegeFor(En_Privilege.SUBNET_EDIT)) {
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

    private AppEvents.InitDetails initDetails;
}