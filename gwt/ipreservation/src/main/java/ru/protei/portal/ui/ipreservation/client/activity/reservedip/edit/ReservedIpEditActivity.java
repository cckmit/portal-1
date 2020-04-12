package ru.protei.portal.ui.ipreservation.client.activity.reservedip.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Date;
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

        if (!hasPrivileges()) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

/*        resetView();*/
        this.reservedIp = event.reservedIp;

        fillView();
    }

    @Override
    public void onSaveClicked() {
        if (!hasPrivileges() || !validateView()) {
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
                    fireEvent(new IpReservationEvents.ChangedReservedIp(reservedIp, true));
                    fireEvent(new IpReservationEvents.CloseEdit());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new IpReservationEvents.CloseEdit());
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

    private void fillView() {
        view.setAddress(reservedIp.getIpAddress());
        view.macAddress().setValue(reservedIp.getMacAddress());
        view.useRange().setValue(new DateInterval(reservedIp.getReserveDate(), reservedIp.getReleaseDate()));
        view.comment().setText(reservedIp.getComment());
        view.owner().setValue(reservedIp.getOwner().toFullNameShortView());

        view.saveVisibility().setVisible(hasPrivileges());
    }

    private ReservedIp fillReservedIp() {
        String macAddress = view.macAddress().getValue() == null || view.macAddress().getValue().trim().isEmpty() ?
                null : view.macAddress().getValue().trim();
        reservedIp.setMacAddress(macAddress);
        reservedIp.setOwnerId(view.owner().getValue().getId());
        reservedIp.setComment(view.comment().getText().trim());
        DateInterval reservedFor = view.useRange().getValue();
        reservedIp.setReserveDate(reservedFor.from);
        reservedIp.setReleaseDate(reservedFor.to);
        return reservedIp;
    }

    private boolean validateView() {
/*        if(!view.macAddress().isValid()){
            fireEvent(new NotifyEvents.Show(lang.errMacAddress(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
*/
        if(view.useRange() == null || view.useRange().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpUseInterval(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        Date from = view.useRange().getValue().from;
        Date to = view.useRange().getValue().to;

        if ( from == null ||
             (!policyService.hasPrivilegeFor(En_Privilege.SUBNET_CREATE) && to == null) ||
              from.after(to)) {
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpUseInterval(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.owner().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpNeedSelectOwner(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private boolean hasPrivileges() {
        if (policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT)
                || reservedIp.getOwnerId().equals(policyService.getProfile().getId())) {
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