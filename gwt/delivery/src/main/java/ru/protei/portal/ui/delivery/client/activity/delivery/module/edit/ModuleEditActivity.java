package ru.protei.portal.ui.delivery.client.activity.delivery.module.edit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ModuleControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.view.delivery.module.namedescription.ModuleNameDescriptionButtonsView;
import ru.protei.portal.ui.delivery.client.view.delivery.module.namedescription.ModuleNameDescriptionEditView;
import ru.protei.portal.ui.delivery.client.view.delivery.module.namedescription.ModuleNameDescriptionView;

import java.util.List;
import java.util.function.Consumer;

import static ru.protei.portal.ui.common.client.util.MultiTabWidgetUtils.getCommentAndHistorySelectedTabs;
import static ru.protei.portal.ui.common.client.util.MultiTabWidgetUtils.saveCommentAndHistorySelectedTabs;

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
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(ModuleEvents.ShowPreview event) {
        HasWidgets container = event.parent;
        if (!hasViewPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        requestModule(event.id, container, true);
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(ModuleEvents.Edit event) {
        if (!hasViewPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        Window.scrollTo(0, 0);
        requestModule(event.id, initDetails.parent, false);
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

    @Override
    public void onOpenEditViewClicked() {
        fireEvent(new ModuleEvents.Edit(module.getId()));
    }

    @Override
    public void onBackClicked() {
        fireEvent(new KitEvents.Show(module.getDeliveryId(), module.getKitId()));
    }

    @Override
    public void onSelectedTabsChanged(List<En_CommentOrHistoryType> selectedTabs) {
        saveCommentAndHistorySelectedTabs(localStorageService, selectedTabs);
        fireEvent(new CommentAndHistoryEvents.ShowItems(commentAndHistoryView, selectedTabs));
    }

    private void fillView(Module module) {
        view.setCreatedBy(lang.createBy(module.getCreator().getDisplayShortName(),
                DateFormatter.formatDateTime(module.getCreated())));
        view.setModuleNumber(module.getSerialNumber());
        nameAndDescriptionView.setName(module.getName());
        nameAndDescriptionView.setDescription(module.getDescription());
        view.nameAndDescriptionEditButtonVisibility().setVisible(hasEditPrivileges());

        view.getMultiTabWidget().selectTabs(getCommentAndHistorySelectedTabs(localStorageService));
        view.getItemsContainer().clear();
        view.getItemsContainer().add(commentAndHistoryView.asWidget());
        fireEvent(new CommentAndHistoryEvents.Show(commentAndHistoryView, module.getId(),
                En_CaseType.MODULE, true, module.getCreatorId()));

        renderMarkupText(module.getDescription(), En_TextMarkup.MARKDOWN, html -> nameAndDescriptionView.setDescription(html));
    }

    private void requestModule(Long id, HasWidgets container, boolean isPreviewMode) {
        moduleService.getModule(id, new FluentCallback<Module>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(module -> {
                    this.module = module;
                    switchNameDescriptionToEdit(false);
                    fillView(module);
                    showMeta(module);
                    attachToContainer(container);
                    viewModeIsPreview(isPreviewMode);
                }));
    }

    private void showMeta(Module module) {
        fireEvent(new ModuleEvents.EditModuleMeta(view.getMetaContainer(), module, !hasEditPrivileges()));
    }

    private void attachToContainer(HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
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

    private void renderMarkupText(String text, En_TextMarkup markup, Consumer<String> consumer ) {
        textRenderController.render( text, markup, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private void viewModeIsPreview(boolean isPreviewMode){
        view.backButtonVisibility().setVisible(!isPreviewMode);
        view.showEditViewButtonVisibility().setVisible(isPreviewMode);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT);
    }

    private boolean hasViewPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW);
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
    private AbstractCommentAndHistoryListView commentAndHistoryView;

    @Inject
    PolicyService policyService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;

    @ContextAware
    Module module;

    private boolean requestedNameDescription;
    private CaseNameAndDescriptionChangeRequest changeRequest;
    private AppEvents.InitDetails initDetails;
}

