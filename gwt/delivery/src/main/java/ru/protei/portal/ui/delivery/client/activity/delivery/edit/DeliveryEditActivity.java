package ru.protei.portal.ui.delivery.client.activity.delivery.edit;

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
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DeliveryControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.handler.KitActionsHandler;
import ru.protei.portal.ui.delivery.client.view.delivery.namedescription.DeliveryNameDescriptionButtonsView;
import ru.protei.portal.ui.delivery.client.view.delivery.namedescription.DeliveryNameDescriptionEditView;
import ru.protei.portal.ui.delivery.client.view.delivery.namedescription.DeliveryNameDescriptionView;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;
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
        view.setKitsActionHandler(kitActionsHandler);
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
    public void onShow(DeliveryEvents.ShowPreview event) {
        HasWidgets container = event.parent;
        if (!hasAccess()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        viewModeIsPreview(true);
        requestDelivery(event.id, container);
    }

    @Event
    public void onShow(DeliveryEvents.ShowFullScreen event) {
        HasWidgets container = initDetails.parent;
        if (!hasAccess()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        viewModeIsPreview(false);
        requestDelivery(event.deliveryId, container);
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
                    fireEvent(new DeliveryEvents.Change(changeRequest.getId()));
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
    public void onKitEditClicked(Long kitId, String kitName) {
        fireEvent(new KitEvents.Show(delivery.getId(), kitId));
    }

    @Override
    public void onAddKitsButtonClicked() {
        if (!hasCreatePrivileges()) {
            return;
        }
        if (delivery == null || !isMilitaryProject(delivery.getProject())) {
            return;
        }
        fireEvent(new KitEvents.Add(delivery.getId())
                .withBackHandler(kits -> {
                    if (kits.stream().anyMatch(kit -> !Objects.equals(kit.getDeliveryId(), delivery.getId()))) {
                        return;
                    }
                    delivery.setKits(kits);
                    view.fillKits(kits);
                }));
    }

    @Override
    public void onSelectedTabsChanged(List<En_CommentOrHistoryType> selectedTabs) {
        saveCommentAndHistorySelectedTabs(localStorageService, selectedTabs);
        fireEvent(new CommentAndHistoryEvents.ShowItems(commentAndHistoryView, selectedTabs));
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
        view.setKitActionsEnabled(hasEditPrivileges());

        view.getMultiTabWidget().selectTabs(getCommentAndHistorySelectedTabs(localStorageService));

        view.nameAndDescriptionEditButtonVisibility().setVisible(hasEditPrivileges() && isSelfDelivery(delivery.getCreatorId()));
        view.addKitsButtonVisibility().setVisible(hasCreatePrivileges() && isMilitaryProject(delivery.getProject()));

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
        fireEvent(new DeliveryEvents.EditMeta(view.getMetaContainer(), delivery, makeMetaNotifiers(delivery)));
    }

    private void viewModeIsPreview(boolean isPreviewMode){
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

    private boolean hasCreatePrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE);
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

    private boolean isNew(Delivery delivery) {
        return delivery.getId() == null;
    }

    private boolean isOneKitSelected(Set<Kit> kitsSelected) {
        return kitsSelected != null && kitsSelected.size() == 1;
    }

    private void updateKit(final Kit newKit) {
        delivery.getKits().stream()
                .filter(kit -> Objects.equals(kit, newKit))
                .findFirst()
                .ifPresent(kit -> {
                    kit.setState(newKit.getState());
                    kit.setName(newKit.getName());
                    view.updateKit(kit);
                });
    }

    private KitActionsHandler kitActionsHandler = new KitActionsHandler() {
        @Override
        public void onCopy() {
            if (isOneKitSelected(view.getKitsSelected())) {
                fireEvent(new NotifyEvents.Show("On copy Kits clicked", NotifyEvents.NotifyType.SUCCESS));
                return;
            }
            fireEvent(new NotifyEvents.Show(lang.warnOneKitAllowedForTheOperation(), NotifyEvents.NotifyType.ERROR));
        }

        @Override
        public void onGroupChangeState(CaseState state) {

            List<Long> kitsIds = toList(view.getKitsSelected(), Kit::getId);

            if (isEmpty(kitsIds)){
                fireEvent(new NotifyEvents.Show(lang.kitNotSelectedMessage(), NotifyEvents.NotifyType.ERROR));
                return;
            }

            controller.updateKitListStates(kitsIds, state.getId(), new FluentCallback<Void>()
                    .withSuccess((Void) -> controller.getDelivery(delivery.getId(),
                            new FluentCallback<Delivery>()
                                    .withSuccess(delivery -> {
                                        view.fillKits(delivery.getKits());
                                        fireEvent(new NotifyEvents.Show(lang.kitsStatesUpdated(), NotifyEvents.NotifyType.SUCCESS));
                                    }))));
        }

        @Override
        public void onGroupRemove() {
            Set<Kit> kitsSelected = view.getKitsSelected();

            if (isEmpty(kitsSelected)){
                fireEvent(new NotifyEvents.Show(lang.kitNotSelectedMessage(), NotifyEvents.NotifyType.ERROR));
                return;
            }

            String selectedNumbers = stream(kitsSelected).map(Kit::getSerialNumber).collect(Collectors.joining(","));
            fireEvent(new NotifyEvents.Show("On remove Kits clicked, selected kits: " + selectedNumbers, NotifyEvents.NotifyType.SUCCESS));
        }

        @Override
        public void onBack() {}

        @Override
        public void onEdit() {
            if (isOneKitSelected(view.getKitsSelected())) {
                final Long selectedKitId = stream(view.getKitsSelected()).findFirst().map(Kit::getId).get();
                fireEvent(new KitEvents.Edit(selectedKitId)
                        .withBackHandler(kit -> {
                            if (delivery == null || !Objects.equals(delivery.getId(), kit.getDeliveryId())) {
                                return;
                            }
                            updateKit(kit);
                        }));
                return;
            }
            fireEvent(new NotifyEvents.Show(lang.warnOneKitAllowedForTheOperation(), NotifyEvents.NotifyType.ERROR));
        }
    };

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
