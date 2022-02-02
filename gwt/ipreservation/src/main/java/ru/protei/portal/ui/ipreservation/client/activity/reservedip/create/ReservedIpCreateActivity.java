package ru.protei.portal.ui.ipreservation.client.activity.reservedip.create;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.ReservedIpRequest;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.SubnetOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.IpReservationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.ipreservation.client.view.widget.mode.En_ReservedMode;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if (!hasCreatePrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        event.parent.clear();
        event.parent.add(view.asWidget());

        resetView();
    }

    @Override
    public void onSaveClicked() {
        if (!hasCreatePrivileges() || !validateView()) {
            return;
        }

        view.saveEnabled().setEnabled(false);
        fireEvent(new NotifyEvents.Show(lang.reservedIpReservationStart(), NotifyEvents.NotifyType.INFO));

        ReservedIpRequest reservedIpRequest = fillReservedIpRequest();

        ipReservationService.createReservedIp(reservedIpRequest, new FluentCallback<List<ReservedIp>>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(isCreateAvailable());
                    showErrorFromServer(throwable);
                })
                .withSuccess(reservedIpList -> {
                    view.saveEnabled().setEnabled(isCreateAvailable());
                    if (reservedIpList.isEmpty()) {
                        fireEvent(new NotifyEvents.Show(lang.errNotCreated(), NotifyEvents.NotifyType.ERROR));
                    } else {
                        fireEvent(new NotifyEvents.Show(lang.reservedIpPartiallyCreated(reservedIpList.size(),
                                reservedIpRequest.getNumber().intValue()), NotifyEvents.NotifyType.SUCCESS));
                        fireEvent(new IpReservationEvents.ShowReservedIp(true));
                        fireEvent(new IpReservationEvents.CloseEdit());
                    }
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
        view.saveEnabled().setEnabled(isCreateAvailable());
        setSubnetValid();
    }

    @Override
    public void onSubnetsChanged() {
        setSubnetValid();
        checkCreateAvailable();
    }

    @Override
    public void onOwnerChanged() {
        view.ownerValidator().setValid(view.owner().getValue() != null);
        showCreateAvailable();
    }

    @Override
    public void onChangeIpAddress() {
        view.setIpAddressStatus(NameStatus.UNDEFINED);

        if (!view.ipAddressValidator().isValid()) {
            view.setIpAddressStatus(NameStatus.NONE);
            return;
        }

        ipReservationService.isReservedIpAddressExists(
                view.ipAddress().getValue().trim(),
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

    @Override
    public void checkCreateAvailable() {
        if (CollectionUtils.isEmpty(view.subnets().getValue())) {
            freeIpCount = 0;
            showCreateAvailable();
            return;
        }

        List<Long> subnetIds = view.subnets().getValue().stream().map(SubnetOption::getId).collect(Collectors.toList());

        ipReservationService.getFreeIpsCountBySubnets(subnetIds,
                new RequestCallback<Long>() {
                    @Override
                    public void onError(Throwable throwable) {
                        freeIpCount = 0;
                        showCreateAvailable();
                    }

                    @Override
                    public void onSuccess(Long count) {
                        freeIpCount = count == null ? 0 : count.intValue();
                        showCreateAvailable();
                    }
                }
        );
    }

    private void resetView () {
        view.ipAddress().setValue("");
        view.macAddress().setValue("");
        view.subnets().setValue(null);
        view.number().setValue(String.valueOf(CrmConstants.IpReservation.MIN_IPS_COUNT));
        checkCreateAvailable();

        view.reservedMode().setValue(En_ReservedMode.EXACT_IP, true);
        view.reserveModeVisibility().setVisible(true);

        if (hasSystemPrivileges()) {
            view.owner().setValue(null);
            view.ownerEnabled().setEnabled(true);
        } else {
            PersonShortView ipOwner = new PersonShortView(
                    policyService.getProfile().getShortName(),
                    policyService.getProfile().getId(),
                    policyService.getProfile().isFired());
            view.owner().setValue(ipOwner);
            view.ownerEnabled().setEnabled(false);
        }
        view.useRange().setValue(new DateIntervalWithType(
                new DateInterval(new Date(), null), En_DateIntervalType.MONTH));
        view.comment().setText("");

        view.ownerValidator().setValid(view.owner().getValue() != null);
        setSubnetValid();
        view.setEnableUnlimited(hasSystemPrivileges());
        view.saveVisibility().setVisible( true );
        view.saveEnabled().setEnabled(isCreateAvailable());

        resetValidationStatus();
    }

    private ReservedIpRequest fillReservedIpRequest() {
        ReservedIpRequest reservedIpRequest = new ReservedIpRequest();
        En_ReservedMode mode = view.reservedMode().getValue();

        switch (mode) {
            case EXACT_IP:
                reservedIpRequest.setExact(true);
                reservedIpRequest.setNumber(new Long(CrmConstants.IpReservation.MIN_IPS_COUNT));
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
                showError(lang.reservedIpWrongIpAddress());
                return false;
            }

            if(StringUtils.isNotBlank(view.macAddress().getValue()) && !view.macAddressValidator().isValid()){
                showError(lang.reservedIpWrongMacAddress());
                return false;
            }
        } else {

            if(!view.subnetValidator().isValid()){
                showError(lang.errSaveReservedIpNeedSelectSubnet());
                return false;
            }

            Long number = StringUtils.isBlank(view.number().getValue()) ?
                    null : new Long(view.number().getValue());

            if(number == null
               || number < CrmConstants.IpReservation.MIN_IPS_COUNT
               || number > CrmConstants.IpReservation.MAX_IPS_COUNT ){
                showError(lang.reservedIpWrongNumber(
                        CrmConstants.IpReservation.MIN_IPS_COUNT,
                        CrmConstants.IpReservation.MAX_IPS_COUNT));
                return false;
            }
        }

        if(!view.ownerValidator().isValid()){
            showError(lang.errSaveReservedIpNeedSelectOwner());
            return false;
        }

        if (view.useRange().getValue() == null) {
            showError(lang.errSaveReservedIpUseInterval());
            return false;
        } else if (view.useRange().getValue().getIntervalType().equals(En_DateIntervalType.FIXED)) {
            if (view.useRange().getValue().getInterval().to == null) {
                showError(lang.errSaveReservedIpUseOpenInterval());
                return false;
            } else if (!view.useRange().getValue().getInterval().isValid()) {
                showError(lang.errSaveReservedIpUseInterval());
                return false;
            }
        }

        return true;
    }

    private void showCreateAvailable() {
        view.setFreeIpCountLabel(freeIpCount);
        view.saveEnabled().setEnabled(isCreateAvailable());
    }

    private boolean isCreateAvailable() {
        boolean isExactMode = Objects.equals(view.reservedMode().getValue(), En_ReservedMode.EXACT_IP);
        boolean isFreeIpsEnough = view.number().getValue() != null &&
                                  freeIpCount > 0 &&
                                  freeIpCount >= Long.parseLong(view.number().getValue());

        return (isExactMode || isFreeIpsEnough) && view.ownerValidator().isValid();
    }

    private void resetValidationStatus() {
        view.setIpAddressStatus(NameStatus.NONE);
    }

    private void setSubnetValid() {
        view.subnetValidator().setValid(CollectionUtils.isNotEmpty(view.subnets().getValue()));
    }

    private boolean hasSystemPrivileges() {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.RESERVED_IP_CREATE);
    }

    private boolean hasCreatePrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_CREATE);
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
    AbstractReservedIpCreateView view;
    @Inject
    IpReservationControllerAsync ipReservationService;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler errorHandler;

    private int freeIpCount = 0;
}
