package ru.protei.portal.ui.ipreservation.client.activity.reservedip.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.IpReservationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

/**
 * Активность карточки резервирования IP адресов
 */
public abstract class ReservedIpCreateActivity implements AbstractReservedIpCreateActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow (IpReservationEvents.CreateReservedIp event) {
        if (!hasPrivileges()) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

        resetView();
    }

    @Override
    public void onSaveClicked() {
        if (!hasPrivileges() || !validateView()) {
            return;
        }

/*        ReservedIpRequest request = fillReservedIpRequest();*/

        view.saveEnabled().setEnabled(false);

        ipReservationService.createReservedIp(fillReservedIpRequest(), new FluentCallback<List<ReservedIp>>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(reservedIpList -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.ChangedReservedIp(reservedIpList));
                    fireEvent(new IpReservationEvents.CloseEdit());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new IpReservationEvents.CloseEdit());
    }

    @Override
    public void onExactIpClicked() {
        view.exaсtIpVisibility().setVisible(true);
    }

    @Override
    public void onAnyFreeIpsClicked() {
        view.anyFreeIpsVisibility().setVisible(true);
    }

    private void resetView () {
        view.ipAddress().setValue("");
        view.number().setValue(null);
        view.macAddress().setValue("");
        view.subnets().setValue(null);
        if (policyService.hasPrivilegeFor(En_Privilege.SUBNET_CREATE)) {
            view.mode().setValue(true);
            view.owner().setValue(null);
            view.ownerEnabled().setEnabled(true);
        } else {
            view.mode().setValue(policyService.hasPrivilegeFor(En_Privilege.SUBNET_CREATE));
            PersonShortView ipOwner = new PersonShortView(
                    policyService.getProfile().getFullName(),
                    policyService.getProfile().getId(),
                    policyService.getProfile().isFired());
            view.owner().setValue(ipOwner);
            view.ownerEnabled().setEnabled(false);
        }
        view.comment().setText("");
        // @todo dates

        view.saveVisibility().setVisible( true );
        view.saveEnabled().setEnabled(true);
    }

    private ReservedIpRequest fillReservedIpRequest() {
        ReservedIpRequest reservedIpRequest = new ReservedIpRequest();

        reservedIpRequest.setExact(view.mode().getValue());
        if (view.mode().getValue()) {
            reservedIpRequest.setIpAddress(view.ipAddress().getValue());
            reservedIpRequest.setMacAddress(view.macAddress().getValue());
        } else {
            reservedIpRequest.setSubnets(view.subnets().getValue());
            reservedIpRequest.setNumber(view.number().getValue());
        }

        reservedIpRequest.setOwnerId(view.owner().getValue().getId());
        reservedIpRequest.setComment(view.comment().getText());
/*        reservedIpRequest.setReserveDate(view.reserveDate().getValue());
        reservedIpRequest.setReleaseDate(view.releaseDate().getValue());*/
        return reservedIpRequest;
    }

    private boolean validateView() {
/*
        if(!view.ipAddress().isValid()){
            fireEvent(new NotifyEvents.Show(lang.errIpAddress(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.number() != null && !view.number().isValid()){
            fireEvent(new NotifyEvents.Show(lang.errIpAddress(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(!view.macAddress().isValid()){
            fireEvent(new NotifyEvents.Show(lang.errMacAddress(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.reserveDate().getValue() != null && view.reserveDate().getValue() > view.releaseDate()){
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpWrongReserveDate(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.releaseDate().getValue() != null && view.releaseDate().getValue() < view.reserveDate()){
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpWrongReleaseDate(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.owner().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpNeedSelectOwner(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        */

        return true;
    }

    private boolean hasPrivileges() {
        if (policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_CREATE)) {
            return true;
        }

        return false;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractReservedIpCreateView view;
    @Inject
    IpReservationControllerAsync ipReservationService;
    @Inject
    PolicyService policyService;
}