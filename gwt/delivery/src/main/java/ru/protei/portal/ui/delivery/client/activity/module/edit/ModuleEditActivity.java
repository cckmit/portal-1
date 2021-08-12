package ru.protei.portal.ui.delivery.client.activity.module.edit;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.ModuleEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ModuleControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.module.namedescription.ModuleNameDescriptionButtonsView;
import ru.protei.portal.ui.delivery.client.view.module.namedescription.ModuleNameDescriptionEditView;
import ru.protei.portal.ui.delivery.client.view.module.namedescription.ModuleNameDescriptionView;

import java.util.function.Consumer;

public abstract class ModuleEditActivity implements Activity, AbstractModuleEditActivity,
        AbstractModuleNameDescriptionEditActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        nameAndDescriptionButtonView.setActivity(this);
        nameAndDescriptionEditView.getButtonContainer().add(nameAndDescriptionButtonView);
        switchNameDescriptionToEdit(false);
    }

    @Event
    public void onShow(ModuleEvents.Show event ) {
        HasWidgets container = event.parent;
        requestModule(event.id, container);

    }

    private void requestModule(Long id, HasWidgets container) {
        moduleService.getModule(id, new FluentCallback<Module>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(module -> {
                    this.module = module;
                    switchNameDescriptionToEdit(false);
                    fillView(module);
                    showMeta(module);
                    attachToContainer(container);
                }));
    }

    @Override
    public void onNameDescriptionChanged() {
        module.setName(changeRequest.getName());
        module.setDescription(changeRequest.getInfo());
        switchNameDescriptionToEdit(false);
        fillView(module);
    }

    @Override
    public void saveModuleNameAndDescription() {
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

        moduleService.updateNameAndDescription( changeRequest, new FluentCallback<Void>()
                .withError( t -> requestedNameDescription = false )
                .withSuccess( result -> {
                    requestedNameDescription = false;

                    fireEvent( new NotifyEvents.Show( lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS ));
                    fireEvent( new ModuleEvents.ChangeModule(changeRequest.getId()) );
                    onNameDescriptionChanged();
                } ) );
    }

    @Override
    public void onNameAndDescriptionEditClicked() {
        nameAndDescriptionEditView.name().setValue(module.getName());
        nameAndDescriptionEditView.description().setValue(module.getDescription());
        view.nameAndDescriptionEditButtonVisibility().setVisible(false);
        changeRequest = new CaseNameAndDescriptionChangeRequest(module.getId(), module.getName(), module.getDescription());
        switchNameDescriptionToEdit(true);
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

    private void fillView(Module module) {
        view.setCreatedBy(lang.createBy(module.getCreator().getDisplayShortName(),
                DateFormatter.formatDateTime(module.getCreated())));
        view.setModuleNumber(module.getSerialNumber());
        nameAndDescriptionView.setName(module.getName());
        nameAndDescriptionView.setDescription(module.getDescription());
        view.nameAndDescriptionEditButtonVisibility().setVisible(true);

        renderMarkupText(module.getDescription(), En_TextMarkup.MARKDOWN, html -> nameAndDescriptionView.setDescription(html));
 }

    private void renderMarkupText(String text, En_TextMarkup markup, Consumer<String> consumer ) {
        textRenderController.render( text, markup, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private void showMeta(Module module) {
        fireEvent(new ModuleEvents.EditModuleMeta(view.getMetaContainer(), module));
    }

    private void attachToContainer(HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
    }

    @Inject
    Lang lang;
    @Inject
    AbstractModuleEditView view;

    @Inject
    private ModuleNameDescriptionView nameAndDescriptionView;
    @Inject
    private ModuleNameDescriptionEditView nameAndDescriptionEditView;
    @Inject
    private ModuleNameDescriptionButtonsView nameAndDescriptionButtonView;
    @Inject
    private ModuleControllerAsync moduleService;

    @Inject
    TextRenderControllerAsync textRenderController;

    @ContextAware
    Module module;

    private boolean requestedNameDescription;
    private CaseNameAndDescriptionChangeRequest changeRequest;
}

