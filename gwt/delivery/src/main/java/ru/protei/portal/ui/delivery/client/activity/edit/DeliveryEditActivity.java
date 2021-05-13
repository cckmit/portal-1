package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.struct.delivery.DeliveryNameAndDescriptionChangeRequest;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.edit.DeliveryNameAndDescriptionView;
import ru.protei.portal.ui.delivery.client.view.edit.DeliveryNameDescriptionEditView;

import java.util.function.Consumer;

public abstract class DeliveryEditActivity implements Activity, AbstractDeliveryEditActivity,
            AbstractDeliveryNameDescriptionEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        nameAndDescriptionEditView.setActivity(this);

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

    @Override
    public void onNameDescriptionChanged() {
        delivery.setName(changeRequest.getName());
        delivery.setDescription(changeRequest.getDescription());
        view.getNameContainer().clear();
        view.getNameContainer().add(nameAndDescriptionView);
        fillView(delivery);
        fireEvent(new DeliveryEvents.ChangeModel());
    }

    @Override
    public void saveIssueNameAndDescription() {
        if (!nameAndDescriptionEditView.getNameValidator().isValid()) {
            fireEvent( new NotifyEvents.Show( lang.errEmptyName(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }
        if (requestedNameDescription) {
            return;
        }
        requestedNameDescription = true;

        changeRequest.setName( nameAndDescriptionEditView.name().getText() );
        changeRequest.setDescription( nameAndDescriptionEditView.description().getValue() );

        controller.saveNameAndDescription( changeRequest, new FluentCallback<Void>()
                .withError( t -> requestedNameDescription = false )
                .withSuccess( result -> {
                    requestedNameDescription = false;

                    fireEvent( new NotifyEvents.Show( lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS ) );
                    onNameDescriptionChanged();
                } ) );
    }

    @Override
    public void onNameAndDescriptionEditClicked() {
        nameAndDescriptionEditView.name().setText(delivery.getName());
        nameAndDescriptionEditView.description().setValue(delivery.getDescription());
        changeRequest = new DeliveryNameAndDescriptionChangeRequest(delivery.getId(), delivery.getName(), delivery.getDescription());
        view.getNameContainer().clear();
        view.getNameContainer().add(nameAndDescriptionEditView);
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
        renderMarkupText(delivery.getDescription(), En_TextMarkup.MARKDOWN, html -> nameAndDescriptionView.setDescription(html));
    }

    private void showMeta(Delivery delivery) {
        fireEvent(new DeliveryEvents.EditDeliveryMeta(view.getMetaContainer(), delivery));
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    private void renderMarkupText(String text, En_TextMarkup markup, Consumer<String> consumer ) {
        textRenderController.render( text, markup, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractDeliveryEditView view;
    @Inject
    private DeliveryNameAndDescriptionView nameAndDescriptionView;
    @Inject
    private DeliveryNameDescriptionEditView nameAndDescriptionEditView;
    @Inject
    private DeliveryControllerAsync controller;
    private boolean requestedNameDescription;
    private DeliveryNameAndDescriptionChangeRequest changeRequest;
    @Inject
    PolicyService policyService;;
    @Inject
    TextRenderControllerAsync textRenderController;

    @ContextAware
    Delivery delivery;

    private AppEvents.InitDetails initDetails;
}
