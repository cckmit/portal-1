package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.edit.DeliveryNameAndDescriptionView;

public abstract class DeliveryEditActivity implements Activity, AbstractDeliveryEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.getNameContainer().add(nameAndDescriptionView);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DeliveryEvents.Edit event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);
        initDetails.parent.add(view.asWidget());

        requestDelivery(event.id);
    }

    private void requestDelivery(Long id) {
        controller.getDelivery(id, new FluentCallback<Delivery>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(delivery -> {
                    DeliveryEditActivity.this.delivery = delivery;
                    fillView(delivery);
                    showMeta(delivery);
                }));
    }

    private void fillView(Delivery delivery) {
        nameAndDescriptionView.setName(delivery.getName());
        nameAndDescriptionView.setDescription(delivery.getDescription());
        view.kits().setValue(delivery.getKits());
    }

    private void showMeta(Delivery delivery) {
        fireEvent(new DeliveryEvents.EditDeliveryMeta(view.getMetaContainer(), delivery));
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractDeliveryEditView view;
    @Inject
    private DeliveryNameAndDescriptionView nameAndDescriptionView;
    @Inject
    private DeliveryControllerAsync controller;
    @Inject
    PolicyService policyService;;

    @ContextAware
    Delivery delivery;

    private AppEvents.InitDetails initDetails;
}
