package ru.protei.portal.ui.common.client.activity.casecomment.list;

import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.TextWithMarkup;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UserIconUtils;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.CaseCommentControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.util.CaseCommentUtils;
import ru.protei.portal.ui.common.client.view.casecomment.item.CaseCommentItemView;
import ru.protei.portal.ui.common.client.widget.timefield.WorkTimeFormatter;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

/**
 * Активность списка комментариев
 */
public abstract class CaseCommentListActivity
        implements Activity,
        AbstractCaseCommentListActivity, AbstractCaseCommentItemActivity {

    @Inject
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                addTempAttachment(attachment);
            }
            @Override
            public void onError() {
                fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        });
        workTimeFormatter = new WorkTimeFormatter(lang);
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.profile = event.profile;
    }

    @Event
    public void onShow(CaseCommentEvents.Show event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        this.caseType = event.caseType;
        this.caseId = event.caseId;
        this.isElapsedTimeEnabled = event.isElapsedTimeEnabled;
        this.isModifyEnabled = event.isModifyEnabled;

        comment = null;
        lastCommentView = null;
        requesting = false;
        tempAttachments.clear();

        view.sendEnabled().setEnabled(true);
        view.message().setValue(makeCommentText(null), true);
        view.messageMarkup().setValue(event.textMarkup);
        view.attachmentContainer().clear();
        view.clearCommentsContainer();
        view.clearTimeElapsed();
        view.timeElapsedVisibility().setVisible(isElapsedTimeEnabled);
        view.timeElapsedTypeVisibility().setVisible(isElapsedTimeEnabled);
        view.setUserIcon(UserIconUtils.getGenderIcon(profile.getGender()));
        view.enabledNewComment(isModifyEnabled);

        caseCommentController.getCaseComments(caseType, caseId, new FluentCallback<List<CaseComment>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(this::fillView)
        );
    }

    @Event
    public void onValidateComment(CaseCommentEvents.ValidateComment event) {
        if (StringUtils.isNotBlank(view.message().getValue())) {
            event.validate(true);
            return;
        }
        if (view.timeElapsed().getTime() != null) {
            event.validate(false);
            return;
        }
        if (!tempAttachments.isEmpty()) {
            event.validate(false);
            return;
        }
        event.validate(true);
    }

    @Event
    public void onSaveComment(CaseCommentEvents.SaveComment event) {
        if (!HelperFunc.isEmpty(view.message().getValue())) {
            send(event.caseId, event.handler);
        } else {
            event.handler.onSuccess();
        }
    }

    @Event
    public void onRemoveDraft(CaseCommentEvents.RemoveDraft event){
        if (event.caseId != null) {
            storage.remove(makeStorageKey(event.caseId));
        }
    }

    @Override
    public void onRemoveClicked(final AbstractCaseCommentItemView itemView ) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        if(caseComment == comment) {
            //deleting while editing
            fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.INFO));
            return;
        }
        if ( caseComment == null || !CaseCommentUtils.isEnableEdit( caseComment, profile.getId() ) ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentNotAllowed(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        lastCommentView = null;

        if (caseComment.getCaseStateId() != null) {
            caseComment.setText(null);
            caseCommentController.saveCaseComment(caseType, caseComment, new FluentCallback<CaseComment>()
                    .withSuccess(comment -> itemView.setMessage(null))
            );
            return;
        }

        caseCommentController.removeCaseComment(caseType, caseComment, new FluentCallback<Void>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errRemoveIssueComment(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(v -> {
                    Collection<Attachment> commentAttachments = itemView.attachmentContainer().getAll();
                    if (CollectionUtils.isNotEmpty(commentAttachments)) {
                        fireEvent(new AttachmentEvents.Remove(caseId, commentAttachments));
                    }
                    view.removeComment(itemView);
                    itemViewToModel.remove(itemView);
                    fireEvent(new IssueEvents.ChangeModel());
                    updateTimeElapsedInIssue(itemViewToModel.values());
                })
        );
    }

    @Override
    public void onEditClicked( AbstractCaseCommentItemView itemView ) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        if ( caseComment == null || !CaseCommentUtils.isEnableEdit( caseComment, profile.getId() ) ) {
            fireEvent( new NotifyEvents.Show( lang.errEditIssueCommentNotAllowed(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        this.comment = caseComment;
        this.lastCommentView = itemView;

        view.attachmentContainer().clear();
        tempAttachments.clear();

        Collection<Attachment> commentAttachments = itemView.attachmentContainer().getAll();
        if(!commentAttachments.isEmpty()) {
            view.attachmentContainer().add(commentAttachments);
            tempAttachments.addAll(commentAttachments);
        }

        String editedMessage = caseComment.getText();
        view.message().setValue( editedMessage, true );
        if (isElapsedTimeEnabled && comment.getTimeElapsed() != null) {
            view.timeElapsed().setTime(comment.getTimeElapsed());
            view.timeElapsedType().setValue(comment.getTimeElapsedType());
        }
        view.focus();
    }

    @Override
    public void onReplyClicked( AbstractCaseCommentItemView itemView ) {
        CaseComment value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        comment = null;

        String message = CaseCommentUtils.appendQuote(view.message().getValue(), value.getText(), view.messageMarkup().getValue());
        view.message().setValue( message, true );
        view.focus();
    }

    @Override
    public void onSendClicked() {
        send( null, null );
    }

    @Override
    public void onEditLastMessage() {
        CaseComment value = itemViewToModel.get( lastCommentView );
        if ( value == null ) {
            return;
        }

        view.message().setValue( value.getText(), true );
    }

    @Override
    public void removeTempAttachment(Attachment attachment) {
        Runnable removeTempAttachmentAction = () -> {
            tempAttachments.remove(attachment);
            view.attachmentContainer().remove(attachment);
        };

        if(comment != null && extractIds(comment.getCaseAttachments()).contains(attachment.getId())){
            removeTempAttachmentAction.run();
        }else {
            //deleting the newly created attachment
            removeAttachment(attachment.getId(), removeTempAttachmentAction);
        }
    }

    @Override
    public void onDetachView() {
        attachmentService.clearUploadedAttachmentsCache(new RequestCallback<Void>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Void aVoid) {}
        });
    }

    @Override
    public void onCommentChanged(String text, En_TextMarkup textMarkup) {
        storage.set(makeStorageKey(caseId), text);
        scheduleChangedPreview();
    }

    @Override
    public void onRemoveAttachment(CaseCommentItemView itemView, Attachment attachment) {
        if(comment != null && comment == itemViewToModel.get( itemView )) {
            //deleting while editing
            fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.INFO));
            return;
        }

        removeAttachment(attachment.getId(), () -> {
            fireEvent(new AttachmentEvents.Remove(caseId, Collections.singletonList(attachment)));

            itemView.attachmentContainer().remove(attachment);
            if(itemView.attachmentContainer().getAll().isEmpty()){
                itemView.showAttachments(false);
            }
        });
    }

    private void removeAttachment(Long id, Runnable successAction){
        attachmentService.removeAttachmentEverywhere(caseType, id, new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if(!result) {
                    onError(null);
                    return;
                }
                successAction.run();
            }
        });
    }

    private void addTempAttachment(Attachment attach){
        view.attachmentContainer().add(attach);
        tempAttachments.add(attach);
    }

    private void fillView(List<CaseComment> comments){
        itemViewToModel.clear();
        view.clearCommentsContainer();
        view.enabledNewComment(isModifyEnabled);

        List<AbstractCaseCommentItemView> views = new ArrayList<>();
        List<TextWithMarkup> values = new ArrayList<>();

        for (CaseComment value : comments) {
            AbstractCaseCommentItemView itemView = makeCommentView( value );
            if (StringUtils.isNotEmpty(value.getText())) {
                views.add(itemView);
                values.add(new TextWithMarkup(value.getText(), value.getTextMarkup()));
            }
            view.addCommentToFront( itemView.asWidget() );
        }

        textRenderController.render(values, new FluentCallback<List<String>>()
                .withSuccess(textList -> {
                    for (int i = 0; i < textList.size(); i++) {
                        views.get(i).setMessage(textList.get(i));
                    }
                    views.clear();
                    values.clear();
                }));
    }

    private AbstractCaseCommentItemView makeCommentView(CaseComment value ) {
        AbstractCaseCommentItemView itemView = issueProvider.get();
        itemView.setActivity( this );

        if ( value.getAuthorId().equals( profile.getId() ) ) {
            itemView.setMine();
        }

        itemView.setDate( DateFormatter.formatDateTime( value.getCreated() ) );
        itemView.setOwner( getOwnerName(value) );
        itemView.setIcon( UserIconUtils.getGenderIcon(value.getAuthor().getGender() ) );
        itemView.setRemoteLink(value.getRemoteLink());

        itemView.clearElapsedTime();
        fillTimeElapsed( value, itemView );

        boolean isStateChangeComment = value.getCaseStateId() != null;
        boolean isImportanceChangeComment = value.getCaseImpLevel() != null;

        if ( StringUtils.isNotEmpty( value.getText() ) ) {
            itemView.setMessage(value.getText());
        }

        if ( HelperFunc.isEmpty( value.getText() ) && ( isStateChangeComment || isImportanceChangeComment)) {
            itemView.hideOptions();
        }

        itemView.enabledEdit(isModifyEnabled && isModifyEnabled);

        if ( isStateChangeComment ) {
            En_CaseState caseState = En_CaseState.getById( value.getCaseStateId() );
            itemView.setStatus( caseState );
        }

        if ( isImportanceChangeComment ) {
            En_ImportanceLevel importance = En_ImportanceLevel.getById(value.getCaseImpLevel());
            itemView.setImportanceLevel(importance);
        }

        bindAttachmentsToComment(itemView, value.getCaseAttachments());

        itemView.enabledEdit( isModifyEnabled && CaseCommentUtils.isEnableEdit( value, profile.getId() ) );
        itemView.enableReply(isModifyEnabled);
        itemViewToModel.put( itemView, value );

        return itemView;
    }

    private void fillTimeElapsed( CaseComment value, AbstractCaseCommentItemView itemView ) {
        if (isElapsedTimeEnabled && value.getTimeElapsed() != null) {
            String timeType = (value.getTimeElapsedType() == null || value.getTimeElapsedType().equals( En_TimeElapsedType.NONE ) ? "" : ", " + timeElapsedTypeLang.getName( value.getTimeElapsedType() ));
            itemView.setTimeElapsed( StringUtils.join(
                    " ( +", workTimeFormatter.asString( value.getTimeElapsed() ), timeType, " )"
                    ).toString()
            );
        }
    }

    private void bindAttachmentsToComment(AbstractCaseCommentItemView itemView, List<CaseAttachment> caseAttachments){
        itemView.attachmentContainer().clear();

        if(caseAttachments == null || caseAttachments.isEmpty()){
            itemView.showAttachments(false);
        }else {
            itemView.showAttachments(true);
            requestAttachments(extractIds(caseAttachments), itemView.attachmentContainer()::add);
        }
    }

    private void requestAttachments(List<Long> ids, Consumer<Collection<Attachment>> addAction){

        attachmentService.getAttachments(caseType, ids, new RequestCallback<List<Attachment>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(List<Attachment> list) {
                if(list == null){
                    onError(null);
                    return;
                }
                addAction.accept(list);
            }
        });
    }

    private void synchronizeAttachments(Collection<Attachment> oldAttachments, Collection<Attachment> newAttachments){
        ArrayList<Attachment> listForRemove = new ArrayList<>(oldAttachments);
        ArrayList<Attachment> listForAdd = new ArrayList<>(newAttachments);
        listForRemove.removeIf(listForAdd::remove);

        if(!listForRemove.isEmpty())
            fireEvent(new AttachmentEvents.Remove(caseId, listForRemove));
        if(!listForAdd.isEmpty())
            fireEvent(new AttachmentEvents.Add(caseId, listForAdd));
    }

    private List<Long> extractIds(Collection<CaseAttachment> list){
        return list == null || list.isEmpty()?
                Collections.emptyList():
                list.stream().map(CaseAttachment::getAttachmentId).collect(Collectors.toList());
    }

    private void send(Long id, CaseCommentEvents.SaveComment.SaveCommentCompleteHandler saveCommentCompleteHandler) {
        if ( requesting ) {
            return;
        }
        requesting = true;
        view.sendEnabled().setEnabled(false);

        if ( comment == null ) {
            comment = new CaseComment();
            comment.setAuthorId( profile.getId() );
        }

        boolean isEdit = comment.getId() != null;

        String message = view.message().getValue();
        if ( isBlank( message ) ) {
            if ( id == null ) {
                fireEvent(new NotifyEvents.Show(lang.errEditIssueCommentEmpty(), NotifyEvents.NotifyType.ERROR));
            }
            requesting = false;
            view.sendEnabled().setEnabled(true);
            return;
        }

        if (comment.getTextMarkup() == null) {
            comment.setTextMarkup(view.messageMarkup().getValue());
        }

        comment.setCaseId( id != null ? id : caseId );
        comment.setText( message );
        comment.setTimeElapsed(view.timeElapsed().getTime());
        En_TimeElapsedType elapsedType = view.timeElapsedType().getValue();
        comment.setTimeElapsedType( elapsedType != null ? elapsedType : En_TimeElapsedType.NONE );
        comment.setCaseAttachments(
                tempAttachments.stream()
                        .map(a -> new CaseAttachment(caseId, a.getId(), isEdit? comment.getId(): null))
                        .collect(Collectors.toList())
        );

        caseCommentController.saveCaseComment(caseType, comment, new FluentCallback<CaseComment>()
                .withResult(() -> {
                    requesting = false;
                    view.sendEnabled().setEnabled(true);
                })
                .withError(throwable -> {
                    if (saveCommentCompleteHandler != null) {
                        saveCommentCompleteHandler.onError(throwable, null);
                        return;
                    }
                    fireEvent(new NotifyEvents.Show(lang.errEditIssueComment(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(result -> {
                    storage.remove(makeStorageKey(result.getCaseId()));

                    if (saveCommentCompleteHandler != null) {
                        saveCommentCompleteHandler.onSuccess();
                        return;
                    }
                    result.setCaseAttachments(comment.getCaseAttachments());

                    if (isEdit) {
                        renderTextAsync(result.getText(), result.getTextMarkup(), lastCommentView::setMessage);
                        lastCommentView.clearElapsedTime();
                        fillTimeElapsed( comment, lastCommentView );

                        Collection<Attachment> prevAttachments = lastCommentView.attachmentContainer().getAll();

                        if (!(prevAttachments.isEmpty() && tempAttachments.isEmpty())) {
                            synchronizeAttachments(prevAttachments, tempAttachments);
                            lastCommentView.attachmentContainer().clear();
                            lastCommentView.attachmentContainer().add(tempAttachments);
                            lastCommentView.showAttachments(!tempAttachments.isEmpty());
                        }
                    } else {
                        fireEvent(new AttachmentEvents.Add(caseId, tempAttachments));
                        AbstractCaseCommentItemView itemView = makeCommentView(result);
                        lastCommentView = itemView;
                        view.addCommentToFront(itemView.asWidget());
                        renderTextAsync(result.getText(), result.getTextMarkup(), itemView::setMessage);
                    }

                    comment = null;
                    view.message().setValue(null, true);
                    view.attachmentContainer().clear();
                    view.clearTimeElapsed();
                    tempAttachments.clear();
                    // FIXME зачем IssueEvents.ChangeModel здесь \/
                    fireEvent(new IssueEvents.ChangeModel());
                    updateTimeElapsedInIssue(itemViewToModel.values());
                })
        );
    }

    private void updateTimeElapsedInIssue(Collection<CaseComment> comments) {
        Long timeElapsed = CollectionUtils.stream(comments).filter(cmnt -> cmnt.getTimeElapsed() != null)
                .mapToLong(cmnt -> cmnt.getTimeElapsed()).sum();
        fireEvent( new IssueEvents.ChangeTimeElapsed(timeElapsed) );
    }

    private String getOwnerName(CaseComment caseComment) {
        if (!StringUtils.isEmpty(caseComment.getOriginalAuthorName()))
            return caseComment.getOriginalAuthorName();
        if (caseComment.getAuthor() != null)
            return caseComment.getAuthor().getDisplayName();
        return "Unknown";
    }

    private void scheduleChangedPreview() {
        changedPreviewTimer.cancel();
        changedPreviewTimer.schedule(PREVIEW_CHANGE_DELAY_MS);
    }

    private void fireChangedPreview() {

        String text = view.message().getValue();
        En_TextMarkup textMarkup = view.messageMarkup().getValue();

        if (StringUtils.isBlank(text)) {
            view.setPreviewVisible(false);
            return;
        }

        renderTextAsync(text, textMarkup, converted -> {
            if (StringUtils.isBlank(converted)) {
                view.setPreviewVisible(false);
                return;
            }
            view.setPreviewText(converted);
            view.setPreviewVisible(true);
        });
    }

    private String makeCommentText(String commentText){
        String text = storage.get(makeStorageKey(caseId));
        return isEmpty(text) ? commentText : text;
    }

    private String makeStorageKey(Long id){
        return STORAGE_CASE_COMMENT_PREFIX + id;
    }

    private void renderTextAsync(String text, En_TextMarkup textMarkup, Consumer<String> consumer) {
        String escapedText = HTMLHelper.htmlEscapeWOThreeBackticks(text);
        textRenderController.render(escapedText, textMarkup, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(escapedText))
                .withSuccess(consumer));
    }

    private final Timer changedPreviewTimer = new Timer() {
        @Override
        public void run() {
            fireChangedPreview();
        }
    };

    @Inject
    Lang lang;
    @Inject
    TimeElapsedTypeLang timeElapsedTypeLang;
    @Inject
    CaseCommentControllerAsync caseCommentController;
    @Inject
    AbstractCaseCommentListView view;
    @Inject
    Provider<AbstractCaseCommentItemView> issueProvider;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    TextRenderControllerAsync textRenderController;

    private CaseComment comment;
    private AbstractCaseCommentItemView lastCommentView;
    private WorkTimeFormatter workTimeFormatter;

    private Profile profile;

    private En_CaseType caseType;
    private boolean requesting = false;
    private boolean isElapsedTimeEnabled = false;
    private boolean isModifyEnabled = true;
    private Long caseId;
    
    private Map<AbstractCaseCommentItemView, CaseComment> itemViewToModel = new HashMap<>();
    private Collection<Attachment> tempAttachments = new ArrayList<>();

    private final static int PREVIEW_CHANGE_DELAY_MS = 200;

    @Inject
    private LocalStorageService storage;
    private final String STORAGE_CASE_COMMENT_PREFIX = "CaseСomment_";
}
