package ru.protei.portal.ui.ipreservation.client.activity.reservedip.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.IpReservationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.ipreservation.client.view.widget.mode.En_ReservedMode;

import java.util.Date;
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

        view.saveEnabled().setEnabled(false);

        ipReservationService.createReservedIp(fillReservedIpRequest(), new FluentCallback<List<ReservedIp>>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(reservedIpList -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.ShowReservedIp(true));
                    fireEvent(new IpReservationEvents.CloseEdit());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new IpReservationEvents.CloseEdit());
    }

    @Override
    public void onReservedModeChanged() {
        En_ReservedMode mode = view.reservedMode().getValue();

        view.exaсtIpVisibility().setVisible(En_ReservedMode.EXACT_IP.equals(mode));
        view.anyFreeIpsVisibility().setVisible(En_ReservedMode.ANY_FREE_IPS.equals(mode));
    }

    @Override
    public void onChangeIpAddress() {
        String value = view.ipAddress().getValue().trim();

        if (value.isEmpty()) {
            view.setIpAddressStatus(NameStatus.NONE);
            return;
        }
        ipReservationService.isReservedIpAddressExists(
                value,
                null,
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) { }

                    @Override
                    public void onSuccess(Boolean isExists) {
                        view.setIpAddressStatus(isExists ? NameStatus.ERROR : NameStatus.SUCCESS);
                    }
                }
        );
    }

    private void resetView () {
        view.ipAddress().setValue("");
        view.macAddress().setValue("");
        view.subnets().setValue(null);
        view.number().setValue(null);

        if (policyService.hasPrivilegeFor(En_Privilege.SUBNET_CREATE)) {
            view.reservedMode().setValue(En_ReservedMode.EXACT_IP, true);
            view.reserveModeVisibility().setVisible(true);
            view.owner().setValue(null);
            view.ownerEnabled().setEnabled(true);
        } else {
            view.reservedMode().setValue(En_ReservedMode.ANY_FREE_IPS, true);
            view.reserveModeVisibility().setVisible(false);
            PersonShortView ipOwner = new PersonShortView(
                    policyService.getProfile().getFullName(),
                    policyService.getProfile().getId(),
                    policyService.getProfile().isFired());
            view.owner().setValue(ipOwner);
            view.ownerEnabled().setEnabled(false);
        }
        view.useRange().setValue(new DateIntervalWithType(
                new DateInterval(new Date(), null), En_DateIntervalType.MONTH));
        view.comment().setText("");

        view.saveVisibility().setVisible( true );
        view.saveEnabled().setEnabled(true);

        resetValidationStatus();
    }

    private ReservedIpRequest fillReservedIpRequest() {
        ReservedIpRequest reservedIpRequest = new ReservedIpRequest();
        En_ReservedMode mode = view.reservedMode().getValue();

        switch (mode) {
            case EXACT_IP:
                reservedIpRequest.setExact(true);
                reservedIpRequest.setIpAddress(view.ipAddress().getValue());
                reservedIpRequest.setMacAddress(view.macAddress().getValue());
                break;
            case ANY_FREE_IPS :
                reservedIpRequest.setExact(false);
                reservedIpRequest.setNumber(new Long(view.number().getValue()));
                reservedIpRequest.setSubnets(view.subnets().getValue());
                break;
        }

        reservedIpRequest.setOwnerId(view.owner().getValue().getId());
        reservedIpRequest.setComment(view.comment().getText().trim());

        En_DateIntervalType dateIntervalType = view.useRange().getValue().getIntervalType();
        reservedIpRequest.setDateIntervalType(dateIntervalType);

        if (En_DateIntervalType.FIXED.equals(dateIntervalType)) {
            DateInterval useRange = view.useRange().getValue().getInterval();
            reservedIpRequest.setReserveDate(useRange.from);
            reservedIpRequest.setReleaseDate(useRange.to);
        }

        return reservedIpRequest;
    }

    private boolean validateView() {

        if (En_ReservedMode.EXACT_IP.equals(view.reservedMode().getValue())) {

            if(!view.ipAddressValidator().isValid()){
                fireEvent(new NotifyEvents.Show(lang.reservedIpWrongIpAddress(), NotifyEvents.NotifyType.ERROR));
                return false;
            }

            if(StringUtils.isNotBlank(view.macAddress().getValue()) && !view.macAddressValidator().isValid()){
                Window.alert(view.macAddress().getValue());
                fireEvent(new NotifyEvents.Show(lang.reservedIpWrongMacAddress(), NotifyEvents.NotifyType.ERROR));
                return false;
            }
        } else {
            Long number = view.number().getValue() == null || view.number().getValue().trim().isEmpty() ?
                    null : new Long(view.number().getValue());

            if(number == null
               || number < CrmConstants.IpReservation.MIN_IPS_COUNT
               || number > CrmConstants.IpReservation.MAX_IPS_COUNT ){
                fireEvent(new NotifyEvents.Show(lang.reservedIpWrongNumber(
                        CrmConstants.IpReservation.MIN_IPS_COUNT,
                        CrmConstants.IpReservation.MAX_IPS_COUNT
                ), NotifyEvents.NotifyType.ERROR));
                return false;
            }
        }

        if(view.owner().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpNeedSelectOwner(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.useRange() == null || view.useRange().getValue() == null ||
                ( view.useRange().getValue().getIntervalType().equals(En_DateIntervalType.FIXED) &&
                   !view.useRange().getValue().getInterval().isValid() )
        ) {
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpUseInterval(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private void resetValidationStatus() {
        view.setIpAddressStatus(NameStatus.NONE);
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