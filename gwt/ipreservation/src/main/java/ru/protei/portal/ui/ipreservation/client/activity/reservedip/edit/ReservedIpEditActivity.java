package ru.protei.portal.ui.ipreservation.client.activity.reservedip.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;
import java.util.Objects;

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
        if (event.reservedIp == null || event.reservedIp.getId() == null ) {
            return;
        }

        if (!hasAccess(event.reservedIp)) {
            fireEvent(new IpReservationEvents.CloseEdit());
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

        this.reservedIp = event.reservedIp;

        fillView(reservedIp);
    }

    @Override
    public void onSaveClicked() {
        if (!hasAccess(reservedIp) || !validateView()) {
            return;
        }

        fillReservedIp(reservedIp);

        view.saveEnabled().setEnabled(false);

        ipReservationService.updateReservedIp(reservedIp, new FluentCallback<ReservedIp>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    showErrorFromServer(throwable);
                })
                .withSuccess(aVoid -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.ShowReservedIp());
                    fireEvent(new IpReservationEvents.CloseEdit());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new IpReservationEvents.CloseEdit());
    }

    @Override
    public void onRangeChanged() {
        DateInterval value = view.useRange().getValue();
        if (value == null) {
            return;
        }

        ipReservationService.isReservedIpAddressExists(
                reservedIp.getIpAddress(),
                value.from,
                value.to,
                En_DateIntervalType.FIXED,
                reservedIp.getId(),
                new FluentCallback<Boolean>()
                        .withSuccess(result -> {
                            view.useRangeErrorLabelVisibility().setVisible(result);
                            view.saveEnabled().setEnabled(!result);
                        })
        );
    }

    private void fillView(ReservedIp reservedIp) {
        view.setAddress(reservedIp.getIpAddress());
        view.macAddress().setValue(reservedIp.getMacAddress());
        view.useRange().setValue(new DateInterval(reservedIp.getReserveDate(), reservedIp.getReleaseDate()));
        view.comment().setText(reservedIp.getComment());
        view.lastActiveDate().setText(DateFormatter.formatDateTime(reservedIp.getLastActiveDate()));
        view.lastCheckInfo().setText(reservedIp.getLastCheckInfo());
        PersonShortView ipOwner = new PersonShortView(
                reservedIp.getOwnerName(),
                reservedIp.getOwnerId());
        view.owner().setValue(ipOwner);

        view.saveVisibility().setVisible(hasAccess(reservedIp));
    }

    private ReservedIp fillReservedIp(ReservedIp reservedIp) {
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
        if(StringUtils.isNotBlank(view.macAddress().getValue())
           && !view.macAddressValidator().isValid()){
            showError(lang.reservedIpWrongMacAddress());
            return false;
        }

        if(view.useRange().getValue() == null) {
            showError(lang.errSaveReservedIpUseInterval());
            return false;
        }

        Date from = view.useRange().getValue().from;
        Date to = view.useRange().getValue().to;

        boolean isAllowToSetNullDate = policyService.hasSystemScopeForPrivilege(En_Privilege.RESERVED_IP_EDIT)
                || null == reservedIp.getReleaseDate();

        if ( from == null
             || (to != null && from.after(to))) {
            showError(lang.errSaveReservedIpUseInterval());
            return false;
        }

        if (!isAllowToSetNullDate && to == null) {
            showError(lang.errSaveReservedIpUseOpenInterval());
            return false;
        }

        if(view.owner().getValue() == null){
            showError(lang.errSaveReservedIpNeedSelectOwner());
            return false;
        }

        return true;
    }

    private boolean hasAccess(ReservedIp reservedIp) {
        boolean isAdmin = policyService.hasSystemScopeForPrivilege(En_Privilege.RESERVED_IP_EDIT);
        boolean isUserWithAccess = policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT)
                && Objects.equals(reservedIp.getOwnerId(), policyService.getProfile().getId());
        return isAdmin || isUserWithAccess;
    }

    private void showErrorFromServer(Throwable throwable) {
        errorHandler.accept(throwable);
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractReservedIpEditView view;
    @Inject
    IpReservationControllerAsync ipReservationService;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler errorHandler;

    private ReservedIp reservedIp;
}
