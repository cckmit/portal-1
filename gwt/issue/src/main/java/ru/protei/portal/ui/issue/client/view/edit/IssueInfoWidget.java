package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.fullview.FullViewAttachmentList;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;

import java.util.function.Consumer;

public class IssueInfoWidget extends Composite {

    @PostConstruct
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        attachmentsHeaderContainer.addDomHandler(event -> {
            if (attachmentsRootContainer.getElement().hasClassName("show")) {
                attachmentsRootContainer.getElement().removeClassName("show");
                localStorageService.set(UiConstants.ATTACHMENTS_PANEL_VISIBILITY, Boolean.FALSE.toString());
            } else {
                attachmentsRootContainer.getElement().addClassName("show");
                localStorageService.set(UiConstants.ATTACHMENTS_PANEL_VISIBILITY, Boolean.TRUE.toString());
            }
        }, ClickEvent.getType());

        ensureDebugIds();
    }

    public void setActivity( AbstractIssueEditActivity activity ) {
        this.activity = activity;
    }

    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    public HasWidgets getHistoryContainer() {
        return historyContainer;
    }

    public HasAttachments attachmentsListContainer() {
        return attachmentListContainer;
    }

    public HasVisibility descriptionReadOnlyVisibility() {
        return descriptionReadOnly;
    }

    public void setDescription( String issueDescription, En_TextMarkup textMarkup ) {
        renderMarkupText(issueDescription, textMarkup, html -> descriptionReadOnly.getElement().setInnerHTML(html));
    }

    public void setCountOfAttachments(int countOfAttachments) {
        attachmentsLabel.setInnerText(lang.attachmentsHeader(String.valueOf(countOfAttachments)));
    }

    public HasVisibility attachmentsRootContainerVisibility() {
        return attachmentsRootContainer;
    }

    public void setAttachmentContainerShow(boolean isShow) {
        if (isShow) {
            attachmentsRootContainer.addStyleName("show");
        } else {
            attachmentsRootContainer.removeStyleName("show");
        }
    }

    @UiHandler("attachmentListContainer")
    public void attachmentContainerRemove(RemoveEvent event) {
        activity.removeAttachment(event.getAttachment());
    }

    private void renderMarkupText( String text, En_TextMarkup markup, Consumer<String> consumer ) {
        textRenderController.render( text, markup, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        descriptionReadOnly.ensureDebugId(DebugIds.ISSUE.DESCRIPTION_FIELD );
        attachmentListContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
    }

    @UiField
    Lang lang;

    @UiField
    HTMLPanel commentsContainer;
    @UiField
    HTMLPanel historyContainer;
    @Inject
    @UiField(provided = true)
    FullViewAttachmentList attachmentListContainer;
    @UiField
    HTMLPanel attachmentsRootContainer;
    @UiField
    HTMLPanel attachmentsHeaderContainer;
    @UiField
    LabelElement attachmentsLabel;
    @UiField
    DivElement attachmentsPanel;
    @UiField
    HTMLPanel descriptionReadOnly;

    @Inject
    TextRenderControllerAsync textRenderController;

    @Inject
    LocalStorageService localStorageService;

    private AbstractIssueEditActivity activity;

    interface IssueInfoWidgetUiBinder extends UiBinder<HTMLPanel, IssueInfoWidget> {
    }

    private static IssueInfoWidgetUiBinder ourUiBinder = GWT.create( IssueInfoWidgetUiBinder.class );
}
