package ru.protei.portal.ui.common.client.activity.casecomment.list;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemListActivity;
import ru.protei.portal.ui.common.client.activity.casecomment.item.AbstractCaseCommentItemView;
import ru.protei.portal.ui.common.client.activity.caselink.CaseLinkProvider;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.service.AttachmentControllerAsync;
import ru.protei.portal.ui.common.client.service.CaseCommentControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.view.casecomment.item.CaseCommentItemView;
import ru.protei.portal.ui.common.client.widget.timefield.WorkTimeFormatter;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.ui.common.client.util.AvatarUtils.getAvatarUrl;
import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;

public abstract class CaseCommentItemListActivity implements Activity, AbstractCaseCommentItemListActivity {
    @Inject
    public void init(Lang lang) {
        workTimeFormatter = new WorkTimeFormatter(lang);
    }

    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        this.profile = event.profile;
    }

    @Event
    public void onInit(CaseCommentItemEvents.Init event) {
        this.caseType = event.caseType;
        this.textMarkup = event.textMarkup;
        this.isPrivateVisible = event.isPrivateVisible;
        this.isElapsedTimeEnabled = event.isElapsedTimeEnabled;
        this.isModifyEnabled = event.isModifyEnabled;
        this.isEditAndDeleteEnabled = event.isEditAndDeleteEnabled;
        this.caseId = event.caseId;
        this.commentsContainer = event.commentsContainer;

        this.makeAllowEditValidationString = event.makeAllowEditValidationString;
        this.makeAllowRemoveValidationString = event.makeAllowRemoveValidationString;
    }

    @Event
    public void onClear(CaseCommentItemEvents.Clear event) {
        itemViewToModel.clear();
    }

    @Event
    public void onFillComments(CaseCommentItemEvents.FillComments event) {
        fillView(commentsContainer, event.comments);
    }

    @Event
    public void onCreateComment(CaseCommentItemEvents.CreateComment event) {
        CaseComment caseComment = event.caseComment;

        AbstractCaseCommentItemView itemView = makeCommentView(caseComment);
        itemView.setVisible(event.isVisible);
        itemViewToModel.put( itemView, caseComment );
        commentsContainer.insert(itemView.asWidget(), 0);
        renderTextAsync(caseComment.getText(), textMarkup, itemView::setMessage);

        updateTimeElapsedInIssue(itemViewToModel.values());
    }

    @Event
    public void onSaveOrUpdateComment(CaseCommentItemEvents.SaveOrUpdateClientComment event) {
        CaseComment comment = event.caseComment;

        AbstractCaseCommentItemView newView = makeCommentView( comment );
        newView.setVisible(event.isVisible);
        AbstractCaseCommentItemView oldView = findItemViewByCommentId( comment.getId() );

        if (oldView != null) {
            replaceCommentView( oldView, newView );
            newView.displayUpdatedAnimation();
            itemViewToModel.remove( oldView );
            itemViewToModel.put( newView, comment );

            requestAttachments(extractIds(emptyIfNull(comment.getCaseAttachments())), attachments -> {
                synchronizeAttachments(emptyIfNull(oldView.attachmentContainer().getAll()), attachments);
            });

            return;
        }

        itemViewToModel.put( newView, comment );
        commentsContainer.insert( newView, 0 );
        newView.displayAddedAnimation();

        requestAttachments(extractIds(emptyIfNull(comment.getCaseAttachments())), attachments -> {
            synchronizeAttachments(Collections.emptyList(), attachments);
        });
    }

    @Event
    public void onRemoveClientComment(CaseCommentItemEvents.RemoveClientComment event) {
        AbstractCaseCommentItemView oldView = findItemViewByCommentId( event.commentId );

        if (oldView == null) {
            return;
        }

        Collection<Attachment> commentAttachments = oldView.attachmentContainer().getAll();

        if (CollectionUtils.isNotEmpty(commentAttachments)) {
            fireEvent(new AttachmentEvents.Remove(caseId, commentAttachments));
        }

        commentsContainer.remove(oldView);
        itemViewToModel.remove(oldView);
    }

    @Event
    public void onShow(CaseCommentItemEvents.Show event) {
        itemViewToModel.keySet().forEach(itemView -> itemView.setVisible(true));
    }

    @Event
    public void onHide(CaseCommentItemEvents.Hide event) {
        itemViewToModel.keySet().forEach(itemView -> itemView.setVisible(false));
    }

    @Override
    public void onRemoveClicked(AbstractCaseCommentItemView itemView) {
        CaseComment caseComment = itemViewToModel.get( itemView );

        String validationString = makeAllowRemoveValidationString.apply(caseComment);
        if (validationString != null) {
            fireEvent(new NotifyEvents.Show(validationString, NotifyEvents.NotifyType.ERROR));
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.commentRemoveConfirmMessage(), removeAction(caseComment, itemView)));
    }

    private Runnable removeAction(CaseComment caseComment, AbstractCaseCommentItemView itemView) {
        return () -> {
            caseCommentController.removeCaseComment(caseType, caseComment, new FluentCallback<Long>()
                    .withSuccess(result -> {
                        Collection<Attachment> commentAttachments = itemView.attachmentContainer().getAll();
                        if (CollectionUtils.isNotEmpty(commentAttachments)) {
                            fireEvent(new AttachmentEvents.Remove(caseId, commentAttachments));
                        }

                        commentsContainer.remove(itemView.asWidget());
                        itemViewToModel.remove(itemView);
                        updateTimeElapsedInIssue(itemViewToModel.values());
                        fireEvent(new CommentAndHistoryEvents.Reload());
                    })
            );
        };
    }

    @Override
    public void onEditClicked(AbstractCaseCommentItemView itemView) {
        CaseComment caseComment = itemViewToModel.get( itemView );
        String validationString = makeAllowEditValidationString.apply(caseComment);
        if (validationString != null) {
            fireEvent(new NotifyEvents.Show(validationString, NotifyEvents.NotifyType.ERROR));
            return;
        }

        fireEvent(new CaseCommentItemEvents.EditComment(caseComment, itemView, (comment, newAttachments) -> {
            renderTextAsync(caseComment.getText(), textMarkup, itemView::setMessage);
            fillTimeElapsed(comment, itemView);

            Collection<Attachment> prevAttachments = itemView.attachmentContainer().getAll();

            if (!(prevAttachments.isEmpty() && caseComment.getCaseAttachments().isEmpty())) {
                synchronizeAttachments(prevAttachments, newAttachments);
                itemView.attachmentContainer().clear();
                itemView.attachmentContainer().add(newAttachments);
                itemView.showAttachments(!newAttachments.isEmpty());
            }

            updateTimeElapsedInIssue(itemViewToModel.values());
        }));
    }

    @Override
    public void onReplyClicked(AbstractCaseCommentItemView itemView) {
        CaseComment value = itemViewToModel.get( itemView );
        if ( value == null ) {
            return;
        }

        fireEvent(new CaseCommentItemEvents.ReplyComment(value.getAuthorId()));
    }

    @Override
    public void onRemoveAttachment(CaseCommentItemView itemView, Attachment attachment) {
        fireEvent(new CaseCommentItemEvents.RemoveAttachment(
                itemViewToModel.get(itemView),
                attachment)
        );
    }

    @Override
    public void onTimeElapsedTypeClicked(AbstractCaseCommentItemView itemView) {
        CaseComment caseComment = itemViewToModel.get(itemView);

        if (caseComment == null) {
            return;
        }

        if (!Objects.equals(caseComment.getAuthorId(), policyService.getProfileId())) {
            return;
        }

        itemView.timeElapsedTypePopupVisibility().setVisible(!itemView.timeElapsedTypePopupVisibility().isVisible());
    }

    private void replaceCommentView(AbstractCaseCommentItemView oldView, AbstractCaseCommentItemView newView) {
        int widgetIndex = commentsContainer.getWidgetIndex(oldView);
        commentsContainer.insert(newView.asWidget(), widgetIndex);
        commentsContainer.remove(widgetIndex + 1);
    }

    private void updateTimeElapsedInIssue(Collection<CaseComment> comments) {
        Long timeElapsed = stream(comments).filter(cmnt -> cmnt.getTimeElapsed() != null)
                .mapToLong(CaseComment::getTimeElapsed).sum();
        fireEvent(new IssueEvents.ChangeTimeElapsed(timeElapsed));
    }

    private void fillView(FlowPanel commentsContainer, List<CaseComment> comments) {
        List<AbstractCaseCommentItemView> views = new ArrayList<>();
        List<String> textList = new ArrayList<>();

        for (CaseComment comment : comments) {
            if (comment.getText() == null) {
                continue;
            }

            AbstractCaseCommentItemView itemView = makeCommentView(comment);
            views.add(itemView);
            textList.add(comment.getText());
            itemViewToModel.put(itemView, comment);
            commentsContainer.insert(itemView.asWidget(), 0);
        }

        textRenderController.render(textMarkup, textList, true, new FluentCallback<List<String>>()
                .withSuccess(converted -> {
                    for (int i = 0; i < converted.size(); i++) {
                        views.get(i).setMessage(converted.get(i));
                    }
                    views.clear();
                    textList.clear();
                })
        );
    }

    private AbstractCaseCommentItemView makeCommentView(CaseComment value) {
        AbstractCaseCommentItemView itemView = commentItemViewProvider.get();
        itemView.setActivity(this);

        String avatarUrl;

        if (value.getAuthorId().equals(profile.getId())) {
            avatarUrl = getAvatarUrl(profile);
        } else {
            itemView.timeElapsedVisibility().setVisible(false);
            avatarUrl = getAvatarUrl(value.getAuthor());
        }

        itemView.setImage(avatarUrl);
        itemView.setDate(DateFormatter.formatDateTime(value.getCreated()));
        itemView.setOwner(getOwnerName(value));

        CaseLink remoteLink = value.getRemoteLink();
        if ( remoteLink != null ) {
            itemView.setRemoteLinkNumber(remoteLink.getRemoteId());
            itemView.setRemoteLinkHref(caseLinkProvider.getLink(remoteLink.getType(), remoteLink.getRemoteId()));
        }

        if (StringUtils.isNotEmpty(value.getText())) {
            itemView.setMessage(value.getText());
        }
        fillTimeElapsed(value, itemView);
        if (isPrivateVisible) {
            itemView.setPrivacyType(value.getPrivacyType());
        }

        bindAttachmentsToComment(itemView, value.getCaseAttachments());

        itemView.setTimeElapsedTypeChangeHandler(event -> updateTimeElapsedType(event.getValue(), value, itemView));

        itemView.enabledEdit(isModifyEnabled && isEditAndDeleteEnabled && (makeAllowEditValidationString.apply(value) == null));
        itemView.enableReply(isModifyEnabled);

        return itemView;
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

    private void fillTimeElapsed( CaseComment value, AbstractCaseCommentItemView itemView ) {
        itemView.timeElapsedVisibility().setVisible(value.getAuthorId().equals(profile.getId()) && value.getTimeElapsed() != null);
        itemView.setTimeElapsedType(value.getTimeElapsedType());
        itemView.setTimeElapsedInfo("");

        if (!isElapsedTimeEnabled) {
            return;
        }

        if (value.getTimeElapsed() == null) {
            return;
        }

        String timeType = (value.getTimeElapsedType() == null || value.getTimeElapsedType().equals( En_TimeElapsedType.NONE ) ? "" : ", " + timeElapsedTypeLang.getName( value.getTimeElapsedType() ));
        itemView.timeElapsedInfoContainerVisibility().setVisible(true);
        itemView.setTimeElapsedInfo( StringUtils.join(
                "+", workTimeFormatter.asString( value.getTimeElapsed() ), timeType
                ).toString()
        );
    }

    private String getOwnerName(CaseComment caseComment) {
        if (!StringUtils.isEmpty(caseComment.getOriginalAuthorName()))
            return transliteration(caseComment.getOriginalAuthorName());
        if (caseComment.getAuthor() != null)
            return transliteration(caseComment.getAuthor().getDisplayName());
        return "Unknown";
    }

    private void renderTextAsync(String text, En_TextMarkup textMarkup, Consumer<String> consumer) {
        textRenderController.render(text, textMarkup, true, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(text))
                .withSuccess(consumer));
    }

    private List<Long> extractIds(Collection<CaseAttachment> list){
        return list == null || list.isEmpty()?
                Collections.emptyList():
                list.stream().map(CaseAttachment::getAttachmentId).collect(Collectors.toList());
    }

    private void requestAttachments(List<Long> ids, Consumer<Collection<Attachment>> addAction) {
        if (CollectionUtils.isEmpty(ids)) {
            addAction.accept(new ArrayList<>());
            return;
        }

        attachmentService.getAttachments(caseType, ids, new RequestCallback<List<Attachment>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Attachment> list) {
                if (list == null) {
                    onError(null);
                    return;
                }
                addAction.accept(list);
            }
        });
    }

    private void updateTimeElapsedType(En_TimeElapsedType type, CaseComment value, AbstractCaseCommentItemView itemView) {
        value.setTimeElapsedType(type);
        caseCommentController.updateCaseTimeElapsedType(value.getId(), type, new FluentCallback<Boolean>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errEditTimeElapsedType(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(updated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fillTimeElapsed(value, itemView);
                })
        );
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

    private AbstractCaseCommentItemView findItemViewByCommentId( Long commentId ) {
        if (commentId == null) return null;
        for (Map.Entry<AbstractCaseCommentItemView, CaseComment> entry : itemViewToModel.entrySet()) {
            if (entry.getValue() == null) continue;
            if (!Objects.equals( entry.getValue().getId(), commentId )) continue;
            return entry.getKey();
        }

        return null;
    }

    @Inject
    Lang lang;

    @Inject
    Provider<AbstractCaseCommentItemView> commentItemViewProvider;

    @Inject
    TextRenderControllerAsync textRenderController;

    @Inject
    CaseLinkProvider caseLinkProvider;

    @Inject
    CaseCommentControllerAsync caseCommentController;

    @Inject
    AttachmentControllerAsync attachmentService;

    @Inject
    TimeElapsedTypeLang timeElapsedTypeLang;

    @Inject
    PolicyService policyService;

    private FlowPanel commentsContainer;
    private Function<CaseComment, String> makeAllowEditValidationString;
    private Function<CaseComment, String> makeAllowRemoveValidationString;

    private En_CaseType caseType;
    private En_TextMarkup textMarkup;
    private Profile profile;
    private boolean isPrivateVisible;
    private boolean isElapsedTimeEnabled;
    private boolean isModifyEnabled;
    private boolean isEditAndDeleteEnabled;
    private Long caseId;

    private WorkTimeFormatter workTimeFormatter;

    private final Map<AbstractCaseCommentItemView, CaseComment> itemViewToModel = new LinkedHashMap<>();
}
