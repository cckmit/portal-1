package ru.protei.portal.ui.delivery.client.activity.create;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.meta.DeliveryCommonMeta;
import ru.protei.portal.ui.delivery.client.view.meta.DeliveryMetaView;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public abstract class DeliveryCreateActivity implements Activity, AbstractDeliveryCreateActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);

        DeliveryMetaView metaView = view.getMetaView();
        commonMeta.setDeliveryMetaView(metaView, view::updateKitByProject);
        view.getMetaView().setActivity(commonMeta);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DeliveryEvents.Create event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        prepare();
    }

    @Override
    public void onSaveClicked() {
        String error = getValidationError();
        if (error != null) {
            showValidationError(error);
            return;
        }
        Delivery delivery = fillDto();
        save(delivery);
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void prepare() {
        view.name().setValue(null);
        view.description().setValue(null);
        fillStateSelector(CrmConstants.State.PRELIMINARY);
        view.type().setValue(null);
        view.hwManager().setValue(null);
        view.qcManager().setValue(null);

        view.project().setValue(null);
        commonMeta.clearProjectSpecificFields();

        view.attribute().setValue(null);
        view.departureDate().setValue(null);
        view.setDepartureDateValid(true);
        view.setSubscribers(Collections.emptySet());

        view.setKitsAddButtonEnabled(hasEditPrivileges());
        view.refreshKitsSerialNumberEnabled().setEnabled(hasEditPrivileges());
        view.kitsClear();
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    private Delivery fillDto() {
        Delivery delivery = new Delivery();
        delivery.setName(view.name().getValue());
        delivery.setDescription(view.description().getValue());
        delivery.setProjectId(view.project().getValue().getId());
        delivery.setInitiatorId(view.initiator().getValue() == null ? null : view.initiator().getValue().getId());
        delivery.setAttribute(view.attribute().getValue());
        delivery.setStateId(view.state().getValue().getId());
        delivery.setType(view.type().getValue());
        delivery.setContractId(view.contract().getValue() == null ? null : view.contract().getValue().getId());
        delivery.setDepartureDate(view.departureDate().getValue());
        delivery.setSubscribers(view.getSubscribers());
        delivery.setKits(view.kits().getValue());
        delivery.setHwManagerId(view.hwManager().getValue() == null ? null : view.hwManager().getValue().getId());
        delivery.setQcManagerId(view.qcManager().getValue() == null ? null : view.qcManager().getValue().getId());
        return delivery;
    }

    private void showValidationError(String error) {
        fireEvent(new NotifyEvents.Show(error, NotifyEvents.NotifyType.ERROR));
    }

    private String getValidationError() {
        if (isBlank(view.name().getValue())) {
            return lang.deliveryValidationEmptyName();
        }

        String error = commonMeta.getValidationError();
        if (error != null) {
            return error;
        }
        CaseState state = view.state().getValue();
         if (!Objects.equals(CrmConstants.State.PRELIMINARY, state.getId())) {
            return lang.deliveryValidationInvalidStateAtCreate();
        }
        if (!view.kitsValidate().isValid()) {
            return lang.deliveryValidationInvalidKits();
        }

        return null;
    }

    private void save(Delivery delivery) {
        save(delivery, throwable -> {}, () -> {
            fireEvent(new Back());
        });
    }

    private void save(Delivery delivery, Consumer<Throwable> onFailure, Runnable onSuccess) {
        view.saveEnabled().setEnabled(false);
        controller.saveDelivery(delivery, new FluentCallback<Delivery>()
            .withError(throwable -> {
                view.saveEnabled().setEnabled(true);
                defaultErrorHandler.accept(throwable);
                onFailure.accept(throwable);
            })
            .withSuccess(id -> {
                view.saveEnabled().setEnabled(true);
                onSuccess.run();
            }));
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
    }

    private void fillStateSelector(Long id) {
        view.state().setValue(new CaseState(id));
        caseStateController.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withSuccess(caseState -> view.state().setValue(caseState)));
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractDeliveryCreateView view;
    @Inject
    private DeliveryCommonMeta commonMeta;
    @Inject
    private DeliveryControllerAsync controller;
    @Inject
    private CaseStateControllerAsync caseStateController;
    @Inject
    private PolicyService policyService;
    @Inject
    private DefaultErrorHandler defaultErrorHandler;

    private AppEvents.InitDetails initDetails;
}
