package ru.protei.portal.ui.ipreservation.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.function.Consumer;

/**
 * Активность карточки редактирования зарезервированного IP
 */
public abstract class ReservedIpEditActivity implements AbstractReservedIpEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow (IpReservationEvents.EditReservedIp event) {
        if (event.reservedIp == null || event.reservedIp.getId() == null) {
            return;
        }

        if (!hasPrivileges(event.reservedIp.getId())) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

        this.reservedIp = event.reservedIp;

        fillView();
    }

    @Override
    public void onSaveClicked() {
        if (isNew() && !policyService.hasPrivilegeFor( En_Privilege.RESERVED_IP_CREATE) ) {
            return;
        }

        if (!isNew() && !policyService.hasPrivilegeFor( En_Privilege.RESERVED_IP_EDIT ) ) {
            return;
        }

        if (!validateView()) {
            return;
        }

        fillReservedIp();

        view.saveEnabled().setEnabled(false);

        ipReservationService.updateReservedIp(reservedIp, new FluentCallback<ReservedIp>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aVoid -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.CloseEdit());
                    fireEvent(new IpReservationEvents.ChangedReservedIp(reservedIp, true));
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new IpReservationEvents.CloseEdit());
    }

    private boolean isNew() {
        return reservedIp.getId() == null;
    }

    private void requestReservedIp(Long reservedIpId, Consumer<ReservedIp> successAction) {
        ipReservationService.getReservedIp( reservedIpId, new RequestCallback<ReservedIp>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(ReservedIp reservedIp) {
                ReservedIpEditActivity.this.reservedIp = reservedIp;
                successAction.accept(reservedIp);
            }
        });
    }

    private void resetView () {
        view.macAddress().setValue("");
        view.subnet().setValue(null);
        view.owner().setValue(null);
        view.comment().setText("");

        view.saveVisibility().setVisible( hasPrivileges(reservedIp == null ? null : reservedIp.getId()) );
        view.saveEnabled().setEnabled(true);
    }

    private void fillView() {
        //view.ipAddress().setValue(reservedIp.getIpAddress());
        view.macAddress().setValue(reservedIp.getMacAddress());
        view.subnet().setValue(reservedIp.getSubnet());
        //view.subnetEnabled().setEnabled(reservedIp.getId() == null);
        view.comment().setText(reservedIp.getComment());
        view.owner().setValue(reservedIp.getOwner().toFullNameShortView());
    }

    private ReservedIp fillReservedIp() {
        reservedIp.setMacAddress(view.macAddress().getValue());
        reservedIp.setComment(view.comment().getText());
        //reservedIp.setReserveDate(view.reserveDate().getValue());
        //reservedIp.setReleaseDate(view.releaseDate().getValue());
        reservedIp.setOwnerId(view.owner().getValue().getId());
        return reservedIp;
    }

    private boolean validateView() {
/*        if(!view.macAddress().isValid()){
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
        }*/

        return true;
    }

    private boolean hasPrivileges(Long reservedIpId) {
        if (isNew() && policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_CREATE)) {
            return true;
        }

        // @todo проверка на принадлежность к сисадминству
        if (!isNew() && (policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT)
                || reservedIp.getOwnerId().equals(policyService.getProfile().getId()))) {
            return true;
        }

        return false;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractReservedIpEditView view;
    @Inject
    IpReservationControllerAsync ipReservationService;
    @Inject
    PolicyService policyService;

    private ReservedIp reservedIp;
}