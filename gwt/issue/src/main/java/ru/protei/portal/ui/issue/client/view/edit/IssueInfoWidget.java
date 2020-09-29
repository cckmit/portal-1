package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.accordion.AccordionWidget;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.attachment.list.fullview.FullViewAttachmentList;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;

import java.util.function.Consumer;

public class IssueInfoWidget extends Composite {

    @PostConstruct
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();

        accordionWidget.setLocalStorageKey(UiConstants.ATTACHMENTS_PANEL_VISIBILITY);
        accordionWidget.setMaxHeight(UiConstants.Accordion.ATTACHMENTS_MAX_HEIGHT);
    }

    public void setActivity( AbstractIssueEditActivity activity ) {
        this.activity = activity;
        attachmentListContainer.setActivity(activity);
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
        accordionWidget.setHeader(lang.attachmentsHeader(String.valueOf(countOfAttachments)));
    }

    public HasVisibility attachmentsVisibility() {
        return accordionWidget;
    }

    public void setPrivateCase(boolean isPrivateCase) {
        attachmentListContainer.setPrivateCase(isPrivateCase);
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
        accordionWidget.setHeaderLabelDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);
        accordionWidget.setCollapseButtonDebugId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.ATTACHMENT_COLLAPSE_BUTTON);
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
    HTMLPanel descriptionReadOnly;
    @Inject
    @UiField(provided = true)
    AccordionWidget accordionWidget;

    @Inject
    TextRenderControllerAsync textRenderController;

    private AbstractIssueEditActivity activity;

    interface IssueInfoWidgetUiBinder extends UiBinder<HTMLPanel, IssueInfoWidget> {
    }

    private static IssueInfoWidgetUiBinder ourUiBinder = GWT.create( IssueInfoWidgetUiBinder.class );
}
