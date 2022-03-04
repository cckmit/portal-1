package ru.protei.portal.ui.delivery.client.activity.cardbatch.edit;

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
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardBatchControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.service.ImportanceLevelControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoEditActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.common.AbstractCardBatchCommonInfoEditView;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.meta.AbstractCardBatchMetaView;

import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.ui.common.client.common.DateFormatter.formatDateOnly;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.ERROR;
import static ru.protei.portal.ui.common.client.events.NotifyEvents.NotifyType.SUCCESS;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;
import static ru.protei.portal.ui.common.client.util.MultiTabWidgetUtils.getCommentAndHistorySelectedTabs;
import static ru.protei.portal.ui.common.client.util.MultiTabWidgetUtils.saveCommentAndHistorySelectedTabs;

public abstract class CardBatchEditActivity implements Activity, AbstractCardBatchEditActivity,
        AbstractCardBatchCommonInfoEditActivity, AbstractCardBatchMetaActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);

        metaView.setActivity(this);
        metaView.typeEnabled().setEnabled(false);
        view.getMetaContainer().add(metaView);

        commonInfoEditView.setActivity(this);
        commonInfoEditView.hidePrevCardBatchInfo();
        commonInfoEditView.hideNumberContainer();
        commonInfoEditView.buttonsContainerVisibility().setVisible(true);
        view.getCommonInfoEditContainer().add(commonInfoEditView);
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
    public void onShow(CardBatchEvents.ShowPreview event) {
        HasWidgets container = event.parent;
        if (!hasViewPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        viewModeIsPreview(true);
        requestCardBatch(event.id, container);
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(CardBatchEvents.Edit event) {
        HasWidgets container = initDetails.parent;
        if (!hasEditPrivileges()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        Window.scrollTo(0, 0);
        viewModeIsPreview(false);
        requestCardBatch(event.id, container);
    }

    @Override
    public void getCaseState(Long id, Consumer<CaseState> success) {
        caseStateService.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withError(defaultErrorHandler)
                .withSuccess(success));
    }

    @Override
    public void onStateChange() {
        if (null == metaView.state().getValue()) {
            return;
        }
        cardBatch.setStateId(metaView.state().getValue().getId());
        onMetaChanged();
    }

    @Override
    public void onDeadlineChanged() {
        Date deadline = metaView.deadline().getValue();
        if (!deadlineChanged(deadline)) {
            return;
        }

        if (!validateDeadline(deadline)) {
            showError(lang.cardBatchDeadlineValidationError());
            return;
        }
        cardBatch.setDeadline(metaView.deadline().getValue() != null? metaView.deadline().getValue().getTime() : null);
        onMetaChanged();
    }

    @Override
    public void onPriorityChange() {
        cardBatch.setImportance(metaView.priority().getValue().getId());
        onMetaChanged();
    }

    @Override
    public void onArticleChanged() {
        if (Objects.equals(metaView.article().getValue(), cardBatch.getArticle())) {
            return;
        }
        if (!metaView.articleIsValid()) {
            return;
        }
        cardBatch.setArticle(metaView.article().getValue());
        onMetaChanged();
    }

    private void attachToContainer(HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
    }

    private void viewModeIsPreview(boolean isPreviewMode) {
        view.backButtonVisibility().setVisible(!isPreviewMode);
        view.setPreviewStyles(isPreviewMode);
    }

    private void onMetaChanged() {
        String error = getMetaValidationError();
        if (error != null) {
            showError(error);
            return;
        }

        saveMeta(cardBatch, view.commonInfoEditContainerVisibility().isVisible());
    }

    private void requestCardBatch(Long cardBatchId, HasWidgets container) {
        cardBatchService.getCardBatch(cardBatchId, new FluentCallback<CardBatch>()
                .withSuccess(cardBatch -> {
                    fillView(cardBatch);
                    attachToContainer(container);
                    switchCommonInfoToEdit(false);
                }));
    }

    private boolean deadlineChanged(Date deadline) {
        return !Objects.equals(formatDateOnly(deadline),
                               formatDateOnly(new Date(cardBatch.getDeadline())));
    }

    private boolean validateDeadline(Date deadline) {
        boolean isValid = deadline != null && deadline.after(new Date());
        metaView.setDeadlineValid(isValid);
        return isValid;
    }


    @Override
    public void onAmountChanged() {
        validateAmount();
    }

    @Override
    public void onSaveCommonInfoClicked() {
        String error = getCommonValidationError();
        if (error != null) {
            showError(error);
            return;
        }
        CardBatch cardBatch = fillCommonInfo();
        saveCommonInfo(cardBatch);
    }

    @Override
    public void onCancelSaveCommonInfoClicked() {
        switchCommonInfoToEdit(false);
    }

    @Override
    public void onBackClicked() {
        fireEvent(new CardBatchEvents.Show(!isNew(cardBatch)));
    }

    @Override
    public void onCommonInfoEditClicked() {
        commonInfoEditView.number().setValue(cardBatch.getNumber());
        commonInfoEditView.amount().setValue(cardBatch.getAmount());
        commonInfoEditView.params().setValue(cardBatch.getParams());
        commonInfoEditView.contractors().setValue(cardBatch.getContractors());
        switchCommonInfoToEdit(true);
    }

    @Override
    public void onSelectedTabsChanged(List<En_CommentOrHistoryType> selectedTabs) {
        saveCommentAndHistorySelectedTabs(localStorageService, selectedTabs);
        fireEvent(new CommentAndHistoryEvents.ShowItems(commentAndHistoryView, selectedTabs));
    }

    private boolean isNew(CardBatch cardBatch) {
        return cardBatch.getId() == null;
    }

    private void switchCommonInfoToEdit(boolean isEdit) {
        view.commonInfoEditButtonVisibility().setVisible(!isEdit);
        view.commonInfoEditContainerVisibility().setVisible(isEdit);
        view.commonInfoContainerVisibility().setVisible(!isEdit);
    }

    private boolean validateAmount() {
        Integer value = commonInfoEditView.amount().getValue();
        boolean isValid = value != null && value > 0;
        commonInfoEditView.setAmountValid(isValid);
        return isValid;
    }

    private void fillView(CardBatch cardBatch) {

        this.cardBatch = cardBatch;
        if (cardBatch == null) return;

        view.commonInfoContainerVisibility().setVisible(true);
        view.setCreatedBy(lang.createBy(cardBatch.getCreator() == null ? "" : transliteration(cardBatch.getCreator().getDisplayShortName()),
                DateFormatter.formatDateTime(cardBatch.getCreated())));
        view.setNumberRO(cardBatch.getNumber());
        view.setAmountRO(String.valueOf(cardBatch.getAmount()));
        view.setParamsRO(cardBatch.getParams());

        if (isNotEmpty(cardBatch.getContractors())) {
            StringBuilder contractorsBuilder = new StringBuilder();
            stream(cardBatch.getContractors())
                    .collect(Collectors.groupingBy(PersonProjectMemberView::getRole,
                             Collectors.mapping(PersonProjectMemberView::getDisplayName, Collectors.joining(", "))))
                    .forEach((role, team) ->
                            contractorsBuilder.append("<b>")
                                    .append(roleTypeLang.getName(role))
                                    .append("</b>: ")
                                    .append(team)
                                    .append("<br/>"));

            view.setContractorsRO(contractorsBuilder.toString());
        } else {
            view.setContractorsRO("");
        }

        metaView.state().setValue(cardBatch.getState());
        fillPrioritySelector(cardBatch.getImportance());
        metaView.type().setValue(new EntityOption(cardBatch.getTypeName(), cardBatch.getTypeId()));
        metaView.article().setValue(cardBatch.getArticle());
        metaView.deadline().setValue(new Date(cardBatch.getDeadline()));

        view.getMultiTabWidget().selectTabs(getCommentAndHistorySelectedTabs(localStorageService));
        view.getItemsContainer().clear();
        view.getItemsContainer().add(commentAndHistoryView.asWidget());
        fireEvent(new CommentAndHistoryEvents.Show(commentAndHistoryView, cardBatch.getId(),
                En_CaseType.CARD_BATCH, true, cardBatch.getCreator().getId()));
    }

    private CardBatch fillCommonInfo() {
        cardBatch.setNumber(commonInfoEditView.number().getValue());
        cardBatch.setAmount(commonInfoEditView.amount().getValue());
        cardBatch.setParams(commonInfoEditView.params().getValue());
        cardBatch.setContractors(commonInfoEditView.contractors().getValue());

        return cardBatch;
    }

    private void showError(String error) {
        fireEvent(new NotifyEvents.Show(error, ERROR));
    }

    private String getMetaValidationError() {
        Date deadline = metaView.deadline().getValue();
        if (!deadlineChanged(deadline)) {
            return null;
        }

        if (!validateDeadline(deadline)) {
            return lang.cardBatchDeadlineValidationError();
        }

        if (null == metaView.type().getValue()) {
            return lang.cardBatchTypeValidationError();
        }

        if (!metaView.articleIsValid()) {
            return lang.cardBatchArticleValidationError();
        }

        return null;
    }

    private String getCommonValidationError() {
        if (isEmpty(commonInfoEditView.number().getValue()) || !commonInfoEditView.isNumberValid()) {
            return lang.cardBatchNumberValidationError();
        }

        if (!validateAmount()) {
            return lang.cardBatchAmountValidationError();
        }

        if (CollectionUtils.isEmpty(commonInfoEditView.contractors().getValue())) {
            return lang.cardBatchContractorsValidationError();
        }

        return null;
    }

    private void saveCommonInfo(CardBatch cardBatch) {
        commonInfoEditView.saveEnabled().setEnabled(false);
        cardBatchService.updateCommonInfo(cardBatch, new FluentCallback<CardBatch>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    commonInfoEditView.saveEnabled().setEnabled(true);
                })
                .withSuccess(batch -> {
                    fireEvent(new NotifyEvents.Show(lang.cardBatchSaved(), SUCCESS));
                    fireEvent(new CardBatchEvents.Change(batch.getId()));
                    commonInfoEditView.saveEnabled().setEnabled(true);
                    switchCommonInfoToEdit(false);
                    fillView(batch);
                }));
    }

    private void saveMeta(CardBatch cardBatch, final boolean isEdit) {
        cardBatchService.updateMeta(cardBatch, new FluentCallback<CardBatch>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(batch -> {
                    fireEvent(new NotifyEvents.Show(lang.cardBatchSaved(), SUCCESS));
                    fireEvent(new CardBatchEvents.Change(batch.getId()));
                    fillView(batch);
                    switchCommonInfoToEdit(isEdit);
                }));
    }

    private boolean hasViewPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.CARD_BATCH_VIEW);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.CARD_BATCH_EDIT);
    }

    private void fillPrioritySelector(Integer id) {
        importanceService.getImportanceLevel( id, new FluentCallback<ImportanceLevel>()
                .withSuccess(new Consumer<ImportanceLevel>() {
                    @Override
                    public void accept(ImportanceLevel level) {
                        metaView.priority().setValue(level);
                    }
                }));
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractCardBatchEditView view;
    @Inject
    AbstractCardBatchCommonInfoEditView commonInfoEditView;
    @Inject
    private AbstractCommentAndHistoryListView commentAndHistoryView;
    @Inject
    AbstractCardBatchMetaView metaView;
    @Inject
    private CardBatchControllerAsync cardBatchService;
    @Inject
    private CaseStateControllerAsync caseStateService;
    @Inject
    private PolicyService policyService;
    @Inject
    ImportanceLevelControllerAsync importanceService;

    @Inject
    private DefaultErrorHandler defaultErrorHandler;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    En_PersonRoleTypeLang roleTypeLang;

    @ContextAware
    CardBatch cardBatch;

    private Profile authProfile;
    private AppEvents.InitDetails initDetails;
}
