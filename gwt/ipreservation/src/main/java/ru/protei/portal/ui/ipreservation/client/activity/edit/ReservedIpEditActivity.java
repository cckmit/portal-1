package ru.protei.portal.ui.ipreservation.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IpReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
    public void onInitDetails(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow (IpReservationEvents.EditReservedIp event) {
        if (!hasPrivileges(event.reservedIpId)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        if (event.reservedIpId == null) {
            reservedIp = new ReservedIp();
            resetView();
        } else {
            resetView();
            requestReservedIp(event.reservedIpId, this::fillView);
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        fillReservedIp(reservedIp);

        view.saveEnabled().setEnabled(false);

        ipReservationService.updateReservedIp(reservedIp, new FluentCallback<Project>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.errInternalError(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aVoid -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IpReservationEvents.ChangeModel());
                    fireEvent(isNew(reservedIp) ? new IpReservationEvents.Show(true) : new Back());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private boolean isNew(ReservedIp reservedIp) {
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
        view.ipAddress().setValue("");
        view.macAddress().setValue("");
        view.owner().setValue(null);
        view.subnet().setValue(null);
        view.reservedDate().setValue(null);
        view.releasedDate().setValue(null);
        view.comment().setText("");

        view.saveVisibility().setVisible( hasPrivileges(reservedIp == null ? null : reservedIp.getId()) );
        view.saveEnabled().setEnabled(true);

        if (reservedIp == null || reservedIp.getId() == null) fillCaseLinks(null);
    }

    private void fillView(ReservedIp reservedIp) {
        view.ipAddress().setValue(reservedIp.getIpAddress());
        view.macAddress().setValue(reservedIp.getMacAddress());
        Subnet subnet = reservedIp.getSubnet();
        view.subnet().setValue(subnet == null ? null : subnet.toEntityOption());
        view.subnetEnabled().setEnabled(reservedIp.getId() == null);
        view.comment().setText(reservedIp.getComment());
        view.owner().setValue(reservedIp.getOwnerId());
    }

    private ReservedIp fillReservedIp(ReservedIp reservedIp) {
        reservedIp.setMacAddress(view.macAddress().getValue());
        reservedIp.setComment(view.comment().getText());
        reservedIp.setReleaseDate(view.releaseDate().getValue());
        reservedIp.setOwnerId(view.owner().getValue().getId());
        return reservedIp;
    }

    private boolean validateView() {
        if(!view.macAddress().isValid()){
            fireEvent(new NotifyEvents.Show(lang.errMacAddress(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.releaseDate().getValue() == null
                || view.releaseDate().getValue() < view.reserveDate()){
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpNeedReleaseDate(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.owner().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errSaveReservedIpNeedSelectOwner(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private boolean hasPrivileges(Long reservedIpId) {
        if (reservedIpId == null && policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_CREATE)) {
            return true;
        }

        if (reservedIpId != null && policyService.hasPrivilegeFor(En_Privilege.RESERVED_IP_EDIT)) {
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
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private ReservedIp reservedIp;

    private AppEvents.InitDetails initDetails;
}