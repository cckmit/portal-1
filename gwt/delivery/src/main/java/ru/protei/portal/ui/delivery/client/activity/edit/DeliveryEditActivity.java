package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.DeliveryStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.delivery.client.view.namedescription.DeliveryNameDescriptionButtonsView;
import ru.protei.portal.ui.delivery.client.view.namedescription.DeliveryNameDescriptionEditView;
import ru.protei.portal.ui.delivery.client.view.namedescription.DeliveryNameDescriptionView;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static ru.protei.portal.ui.common.client.util.MultiTabWidgetUtils.getCommentAndHistorySelectedTabs;
import static ru.protei.portal.ui.common.client.util.MultiTabWidgetUtils.saveCommentAndHistorySelectedTabs;

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
    public void onAuthSuccess(AuthEvents.Success event) {
        this.authProfile = event.profile;
    }

    @Event
    public void onShow( DeliveryEvents.ShowPreview event ) {
        HasWidgets container = event.parent;
        if (!hasAccess()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        viewModeIsPreview(true);
        requestDelivery(event.id, container);
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DeliveryEvents.Edit event) {
        if (!hasAccess()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        Window.scrollTo(0, 0);
        viewModeIsPreview(false);
        requestDelivery(event.id, initDetails.parent);
    }

    @Event
    public void onKitsAdded(KitEvents.Added event) {
        if (delivery == null || !Objects.equals(delivery.getId(), event.deliveryId)) {
            return;
        }
        delivery.setKits(event.kits);
        view.fillKits(event.kits);
    }

    @Event
    public void onKitChanged(KitEvents.Changed event) {
        if (delivery == null || !Objects.equals(delivery.getId(), event.deliveryId)) {
            return;
        }

        controller.getDelivery(event.deliveryId, new FluentCallback<Delivery>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(delivery -> {
                    this.delivery = delivery;
                    view.fillKits(delivery.getKits());
                }));
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
                    fireEvent(new DeliveryEvents.ChangeDelivery(changeRequest.getId()));
                    onNameDescriptionChanged();
                } ) );
    }

    @Override
    public void onBackClicked() {
        fireEvent(new DeliveryEvents.Show(!isNew(delivery)));
    }

    @Override
    public void onOpenEditViewClicked() {
        fireEvent(new DeliveryEvents.Edit(delivery.getId()));
    }

    @Override
    public void onNameAndDescriptionEditClicked() {
        nameAndDescriptionEditView.name().setValue(delivery.getName());
        nameAndDescriptionEditView.description().setValue(delivery.getDescription());
        view.nameAndDescriptionEditButtonVisibility().setVisible(false);
        changeRequest = new CaseNameAndDescriptionChangeRequest(delivery.getId(), delivery.getName(), delivery.getDescription());
        switchNameDescriptionToEdit(true);
    }

    @Override
    public void onSearchKitChanged() {
        final String searchPattern = view.searchKitPattern().getValue().trim();
        view.setKitFilter(StringUtils.isEmpty(searchPattern) ? null : makeKitFilter(searchPattern));
    }

    @Override
    public void onKitEditClicked(Long kitId, String kitName) {
        fireEvent(new KitEvents.Edit(kitId));
    }

    @Override
    public void onKitCloneClicked(Long kitId) {
        fireEvent(new NotifyEvents.Show("Kit id clone clicked: " + kitId, NotifyEvents.NotifyType.SUCCESS));
    }

    @Override
    public void onRemoveKitsButtonClicked(Set<Kit> toBeRemoved) {
        fireEvent(new NotifyEvents.Show("Kits to be deleted: " + StringUtils.join(toBeRemoved, Kit::getSerialNumber,","), NotifyEvents.NotifyType.SUCCESS));
    }

    @Override
    public void onAddKitsButtonClicked() {
        if (delivery == null || delivery.getProject() == null) {
            return;
        }
        fireEvent(new KitEvents.Add(delivery.getId(), delivery.getStateId()));
    }

    @Override
    public void onSelectedTabsChanged(List<En_CommentOrHistoryType> selectedTabs) {
        saveCommentAndHistorySelectedTabs(localStorageService, selectedTabs);
        fireEvent(new CommentAndHistoryEvents.ShowItems(commentAndHistoryView, selectedTabs));
    }

    private Selector.SelectorFilter<Kit> makeKitFilter(String searchPattern) {
        String upperCaseSearchPattern = searchPattern.toUpperCase();
        return kit -> kit != null &&
                (kit.getSerialNumber().toUpperCase().contains(upperCaseSearchPattern)
                        || kit.getName().toUpperCase().contains(upperCaseSearchPattern)
                        || stateLang.getStateName(kit.getState()).toUpperCase().contains(upperCaseSearchPattern));
    }

    private void requestDelivery(Long id, HasWidgets container) {
        controller.getDelivery(id, new FluentCallback<Delivery>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(delivery -> {
                    this.delivery = delivery;
                    switchNameDescriptionToEdit(false);
                    fillView(delivery);
                    showMeta(delivery);
                    attachToContainer(container);
                }));
    }

    private void fillView(Delivery delivery) {
        view.setCreatedBy(lang.createBy(delivery.getCreator() == null ? "" : transliteration(delivery.getCreator().getDisplayShortName()),
                DateFormatter.formatDateTime(delivery.getCreated())));

        nameAndDescriptionView.setName(delivery.getName());
        nameAndDescriptionView.setDescription(delivery.getDescription());

        view.fillKits(delivery.getKits());
        view.searchKitPattern().setValue(null);

        view.getMultiTabWidget().selectTabs(getCommentAndHistorySelectedTabs(localStorageService));

        view.nameAndDescriptionEditButtonVisibility().setVisible(hasEditPrivileges() && isSelfDelivery(delivery.getCreatorId()));
        view.addKitsButtonVisibility().setVisible(hasEditPrivileges() && isMilitaryProject(delivery.getProject()));

        renderMarkupText(delivery.getDescription(), En_TextMarkup.MARKDOWN, html -> nameAndDescriptionView.setDescription(html));

        view.getItemsContainer().clear();
        view.getItemsContainer().add(commentAndHistoryView.asWidget());
        fireEvent(new CommentAndHistoryEvents.Show(commentAndHistoryView, delivery.getId(),
                En_CaseType.DELIVERY, true, delivery.getCreatorId()));
    }

    private boolean isSelfDelivery(Long creatorId) {
        return Objects.equals(creatorId, authProfile.getId());
    }

    private boolean isMilitaryProject(Project project) {
        return project.getCustomerType() == En_CustomerType.MINISTRY_OF_DEFENCE;
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

    private boolean hasAccess() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW);
    }

    private boolean hasEditPrivileges() {
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

    private boolean isNew(Delivery project) {
        return project.getId() == null;
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    @Inject
    private Lang lang;
    @Inject
    private DeliveryStateLang stateLang;
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
    private AbstractCommentAndHistoryListView commentAndHistoryView;

    @Inject
    PolicyService policyService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;

    @ContextAware
    Delivery delivery;

    private Profile authProfile;
    private AppEvents.InitDetails initDetails;
    private boolean requestedNameDescription;
    private CaseNameAndDescriptionChangeRequest changeRequest;
}
