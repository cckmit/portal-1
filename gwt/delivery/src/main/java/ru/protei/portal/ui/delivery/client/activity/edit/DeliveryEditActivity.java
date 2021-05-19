package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DeliveryEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.namedescription.DeliveryNameDescriptionButtonsView;
import ru.protei.portal.ui.delivery.client.view.namedescription.DeliveryNameDescriptionEditView;
import ru.protei.portal.ui.delivery.client.view.namedescription.DeliveryNameDescriptionView;

import java.util.function.Consumer;

public abstract class DeliveryEditActivity implements Activity, AbstractDeliveryEditActivity,
            AbstractDeliveryNameDescriptionEditActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        nameAndDescriptionButtonView.setActivity(this);
        nameAndDescriptionEditView.getButtonContainer().add(nameAndDescriptionButtonView);
        switchNameDescriptionToEdit(false);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( DeliveryEvents.ShowPreview event ) {
        HasWidgets container = event.parent;
        if (!policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        viewModeIsPreview(true);
        requestDelivery(event.id, container);
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DeliveryEvents.Edit event) {
        if (!hasPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        Window.scrollTo(0, 0);
        viewModeIsPreview(false);
        requestDelivery(event.id, initDetails.parent);
    }

    @Override
    public void onNameDescriptionChanged() {
        delivery.setName(changeRequest.getName());
        delivery.setDescription(changeRequest.getInfo());
        switchNameDescriptionToEdit(false);
        fillView(delivery);
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

        changeRequest.setName( nameAndDescriptionEditView.name().getValue() );
        changeRequest.setInfo( nameAndDescriptionEditView.description().getValue() );

        controller.updateNameAndDescription( changeRequest, new FluentCallback<Void>()
                .withError( t -> requestedNameDescription = false )
                .withSuccess( result -> {
                    requestedNameDescription = false;

                    fireEvent( new NotifyEvents.Show( lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS ) );
                    onNameDescriptionChanged();
                } ) );
    }

    @Override
    public void onBackClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onOpenEditViewClicked() {
        fireEvent(new DeliveryEvents.Edit(delivery.getId()));
    }

    @Override
    public void onNameAndDescriptionEditClicked() {
        nameAndDescriptionEditView.name().setValue(delivery.getName());
        nameAndDescriptionEditView.description().setValue(delivery.getDescription());
        changeRequest = new CaseNameAndDescriptionChangeRequest(delivery.getId(), delivery.getName(), delivery.getDescription());
        switchNameDescriptionToEdit(true);
    }

    private void requestDelivery(Long id, HasWidgets container) {
        controller.getDelivery(id, new FluentCallback<Delivery>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(delivery -> {
                    DeliveryEditActivity.this.delivery = delivery;
                    switchNameDescriptionToEdit(false);
                    fillView(delivery);
                    showMeta(delivery);
                    attachToContainer(container);
                }));
    }

    private void fillView(Delivery delivery) {
        nameAndDescriptionView.setName(delivery.getName());
        nameAndDescriptionView.setDescription(delivery.getDescription());
        view.updateKitByProject(delivery.getProject().getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE);
        view.kits().setValue(delivery.getKits());
        renderMarkupText(delivery.getDescription(), En_TextMarkup.MARKDOWN, html -> nameAndDescriptionView.setDescription(html));
    }

    private void showMeta(Delivery delivery) {
        fireEvent(new DeliveryEvents.EditDeliveryMeta(view.getMetaContainer(), delivery, makeMetaNotifiers(delivery)));
    }

    private void viewModeIsPreview( boolean isPreviewMode){
        view.backButtonVisibility().setVisible(!isPreviewMode);
        view.showEditViewButtonVisibility().setVisible(isPreviewMode);
        view.setPreviewStyles(isPreviewMode);
    }

    private void attachToContainer(HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
    }

    private boolean hasPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    private void renderMarkupText(String text, En_TextMarkup markup, Consumer<String> consumer ) {
        textRenderController.render( text, markup, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private CaseObjectMetaNotifiers makeMetaNotifiers(Delivery delivery) {
        return new CaseObjectMetaNotifiers(delivery);
    }

    private void switchNameDescriptionToEdit(boolean isEdit) {
        HasWidgets nameContainer = view.getNameContainer();
        nameContainer.clear();
        if (isEdit) {
            nameContainer.add(nameAndDescriptionEditView);
        } else {
            nameContainer.add(nameAndDescriptionView);
        }
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractDeliveryEditView view;
    @Inject
    private DeliveryNameDescriptionView nameAndDescriptionView;
    @Inject
    private DeliveryNameDescriptionEditView nameAndDescriptionEditView;
    @Inject
    private DeliveryNameDescriptionButtonsView nameAndDescriptionButtonView;
    @Inject
    private DeliveryControllerAsync controller;

    @Inject
    PolicyService policyService;
    @Inject
    TextRenderControllerAsync textRenderController;

    @ContextAware
    Delivery delivery;

    private AppEvents.InitDetails initDetails;
    private boolean requestedNameDescription;
    private CaseNameAndDescriptionChangeRequest changeRequest;
}
