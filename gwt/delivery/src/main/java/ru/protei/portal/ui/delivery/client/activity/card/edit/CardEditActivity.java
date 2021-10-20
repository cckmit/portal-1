package ru.protei.portal.ui.delivery.client.activity.card.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CommentsAndHistories;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseCommentControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils;
import ru.protei.portal.ui.common.client.view.casecomment.list.CommentAndHistoryListView;
import ru.protei.portal.ui.common.client.view.casehistory.item.CaseHistoryItemsContainer;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.delivery.client.view.card.infoComment.CardNoteCommentButtonsView;
import ru.protei.portal.ui.delivery.client.view.card.infoComment.CardNoteCommentEditView;
import ru.protei.portal.ui.delivery.client.view.card.infoComment.CardNoteCommentView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.ui.common.client.util.CommentOrHistoryUtils.getSortedCommentOrHistoryList;

public abstract class CardEditActivity implements Activity, AbstractCardEditActivity,
        AbstractCardNoteCommentEditActivity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        noteCommentButtonsView.setActivity(this);
        noteCommentEditView.getButtonContainer().add(noteCommentButtonsView);
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
    public void onShow(CardEvents.ShowPreview event) {
        HasWidgets container = event.parent;
        if (event.id == null || !hasAccess()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        viewModeIsPreview(true);
        request(event.id, container);
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(CardEvents.Edit event) {
        HasWidgets container = initDetails.parent;
        if (event.id == null || !hasAccess()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        Window.scrollTo(0, 0);
        viewModeIsPreview(false);
        request(event.id, container);
    }

    @Override
    public void onCancelNoteCommentClicked() {
        switchNameDescriptionToEdit(false);
        fillView(card);
        showMeta(card);
    }

    @Override
    public void onSaveNoteCommentClicked() {
        card.setNote(noteCommentEditView.note().getValue());
        card.setComment(noteCommentEditView.comment().getValue());
        controller.updateNoteAndComment(card, new FluentCallback<Card>()
                .withSuccess(result -> {
                    card = result;
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CardEvents.Change(card));
                    onCancelNoteCommentClicked();
                }));
    }

    @Override
    public void onBackClicked() {
        fireEvent(new CardEvents.Show(!isNew(card)));
    }

    @Override
    public void onNoteCommentClicked() {
        noteCommentEditView.note().setValue(card.getNote());
        noteCommentEditView.comment().setValue(card.getComment());
        view.noteCommentEditButtonVisibility().setVisible(false);
        switchNameDescriptionToEdit(true);
    }

    @Event
    public void onUpdate(CardEvents.Change event) {
        if (view.asWidget().isAttached()) {
            fillHistory();
        }
    }

    private void request(Long id, HasWidgets container) {
        controller.getCard(id, new FluentCallback<Card>()
                .withError((throwable, defaultErrorHandler, status) -> defaultErrorHandler.accept(throwable))
                .withSuccess(card -> {
                    this.card = card;
                    switchNameDescriptionToEdit(false);
                    fillView(card);
                    fillHistory();
                    showMeta(card);
                    attachToContainer(container);
                }));
    }

    private void attachToContainer(HasWidgets container) {
        container.clear();
        container.add(view.asWidget());
    }

    private void fillView(Card card) {
        view.setCreatedBy(lang.createBy(card.getCreator() == null ? "" : transliteration(card.getCreator().getDisplayShortName()),
                DateFormatter.formatDateTime(card.getCreated())));

        view.setSerialNumber(card.getSerialNumber());
        noteCommentView.setNote(card.getNote());
        noteCommentView.setComment(card.getComment());

        view.noteCommentEditButtonVisibility().setVisible(hasEditPrivileges());

        renderMarkupText(card.getNote(), html -> noteCommentView.setNote(html));
        renderMarkupText(card.getComment(), html -> noteCommentView.setComment(html));
    }

    private void showMeta(Card card) {
        fireEvent(new CardEvents.EditMeta(card, view.getMetaContainer()));
    }

    private boolean hasAccess() {
        return policyService.hasPrivilegeFor(En_Privilege.CARD_VIEW);
    }

    private boolean hasEditPrivileges() {
        return policyService.hasPrivilegeFor(En_Privilege.CARD_EDIT);
    }

    private void renderMarkupText(String text, Consumer<String> consumer ) {
        textRenderController.render( text, En_TextMarkup.MARKDOWN, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private void switchNameDescriptionToEdit(boolean isEdit) {
        HasWidgets noteCommentContainer = view.getNoteCommentContainer();
        noteCommentContainer.clear();
        if (isEdit) {
            noteCommentContainer.add(noteCommentEditView);
        } else {
            noteCommentContainer.add(noteCommentView);
        }
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private void fillHistory(){
        view.getItemsContainer().clear();
        view.getItemsContainer().add(commentAndHistoryView.asWidget());
        commentAndHistoryView.clearItemsContainer();

        caseCommentController.getCommentsAndHistories(En_CaseType.CARD, card.getId(), new FluentCallback<CommentsAndHistories>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(this::fillHistoryView)
        );
    }

    public void fillHistoryView(CommentsAndHistories commentsAndHistories) {

        List<CommentsAndHistories.CommentOrHistory> commentOrHistoryList
                = getSortedCommentOrHistoryList(commentsAndHistories.getCommentOrHistoryList());

        List<History> histories = stream(commentOrHistoryList)
                .map(CommentsAndHistories.CommentOrHistory::getHistory)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<CaseHistoryItemsContainer> caseHistoryItemsContainers = CommentOrHistoryUtils.fillView(
                histories, commentAndHistoryView.commentsAndHistoriesContainer());

        historyItemsContainers.addAll(caseHistoryItemsContainers);

        commentAndHistoryView.setNewCommentHidden(true);
        historyItemsContainers.forEach(historyItemsContainer -> historyItemsContainer.setVisible(true));
    }

    private void viewModeIsPreview(boolean isPreviewMode) {
        view.backButtonVisibility().setVisible(!isPreviewMode);
        view.setPreviewStyles(isPreviewMode);
    }

    private boolean isNew(Card card) {
        return card.getId() == null;
    }

    @Inject
    private Lang lang;
    @Inject
    private AbstractCardEditView view;
    @Inject
    private CardNoteCommentView noteCommentView;
    @Inject
    private CardNoteCommentEditView noteCommentEditView;
    @Inject
    private CardNoteCommentButtonsView noteCommentButtonsView;
    @Inject
    CommentAndHistoryListView commentAndHistoryView;
    @Inject
    CaseCommentControllerAsync caseCommentController;
    @Inject
    private CardControllerAsync controller;

    @Inject
    PolicyService policyService;
    @Inject
    TextRenderControllerAsync textRenderController;

    @ContextAware
    Card card;
    private final List<CaseHistoryItemsContainer> historyItemsContainers = new ArrayList<>();
    private Profile authProfile;
    private AppEvents.InitDetails initDetails;
}
