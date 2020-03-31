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
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueNameDescriptionEditWidgetActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.dict.En_Privilege.ISSUE_EDIT;

public class IssueNameDescriptionEditWidget extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
        description.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( AbstractIssueEditView.DESCRIPTION, isDisplay ) );
    }

    public void setFileUploader(AttachmentUploader pasteHandler) {
        description.setFileUploader(pasteHandler);
    }

    public HasValue<String> description(){
        return description;
    }

    public void setActivity( AbstractIssueNameDescriptionEditWidgetActivity activity ) {
        this.activity = activity;
    }

    public void setIssueIdNameDescription(CaseNameAndDescriptionChangeRequest changeRequest, En_TextMarkup textMarkup ) {
        this.changeRequest = changeRequest;
        description.setRenderer( ( text, consumer ) -> renderMarkupText( text, textMarkup, consumer ) );
        setDescriptionPreviewAllowed( makePreviewDisplaying( AbstractIssueEditView.DESCRIPTION ) );
        name.setValue( changeRequest.getName() );
        description.setValue( changeRequest.getInfo() );

        tempAttachment.clear();
    }

    public void addTempAttachment(Attachment attachment) {
        tempAttachment.add(attachment);
    }

    @UiHandler("saveNameAndDescriptionButton")
    public void onSaveNameAndDescriptionButtonClick( ClickEvent event ) {
        onSaveNameAndDescriptionClicked();
    }

    @UiHandler("cancelNameAndDescriptionButton")
    public void onCancelNameAndDescriptionButtonClick( ClickEvent event ) {
        activity.onIssueNameInfoChanged( changeRequest );
    }

    private void onSaveNameAndDescriptionClicked() {
        if (!nameValidator.isValid()) {
            activity.fireEvent( new NotifyEvents.Show( lang.errEmptyName(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }
        if (requested) return;
        requested = true;

        changeRequest.setName( name.getValue() );
        changeRequest.setInfo( description.getValue() );
        changeRequest.setAttachments( tempAttachment );

        issueService.saveIssueNameAndDescription( changeRequest, new FluentCallback<Void>()
                .withError( t -> requested = false )
                .withSuccess( result -> {
                    requested = false;

                    activity.fireEvent( new NotifyEvents.Show( lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS ) );
                    activity.onIssueNameInfoChanged( changeRequest );
                } ) );
    }

    private boolean makePreviewDisplaying( String key ) {
        return Boolean.parseBoolean( localStorageService.getOrDefault( ISSUE_EDIT + "_" + key, "false" ) );
    }

    private void renderMarkupText( String text, En_TextMarkup markup, Consumer<String> consumer ) {
        textRenderController.render( text, markup, new FluentCallback<String>()
                .withError( throwable -> consumer.accept( null ) )
                .withSuccess( consumer ) );
    }

    private void setDescriptionPreviewAllowed( boolean isPreviewAllowed ) {
        description.setDisplayPreview( isPreviewAllowed );
    }

    private void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( ISSUE_EDIT + "_" + key, String.valueOf( isDisplay ) );
    }

    private HasValidable nameValidator = new HasValidable() {
        @Override
        public void setValid( boolean isValid ) {
            if (isValid) {
                nameContainer.removeStyleName( HAS_ERROR );
            } else {
                nameContainer.addStyleName( HAS_ERROR );
            }
        }

        @Override
        public boolean isValid() {
            return HelperFunc.isNotEmpty( name.getValue() );
        }
    };

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        name.ensureDebugId( DebugIds.ISSUE.NAME_INPUT );
        description.setEnsureDebugId( DebugIds.ISSUE.DESCRIPTION_INPUT );
        nameLabel.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME );
        descriptionLabel.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO );
        saveNameAndDescriptionButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_ACCEPT);
        cancelNameAndDescriptionButton.ensureDebugId(DebugIds.ISSUE.EDIT_NAME_AND_DESC_REJECT);
    }

    @UiField
    Lang lang;

    @UiField
    TextBox name;
    @UiField
    MarkdownAreaWithPreview description;
    @UiField
    LabelElement descriptionLabel;
    @UiField
    HTMLPanel descriptionContainer;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    LabelElement nameLabel;
    @UiField
    Button saveNameAndDescriptionButton;
    @UiField
    Button cancelNameAndDescriptionButton;
    @UiField
    DivElement nameAndDescriptionButtonsPanel;

    @Inject
    TextRenderControllerAsync textRenderController;

    @Inject
    LocalStorageService localStorageService;

    @Inject
    IssueControllerAsync issueService;

    public static final String HAS_ERROR = "has-error";
    private AbstractIssueNameDescriptionEditWidgetActivity activity;
    private boolean requested;
    private CaseNameAndDescriptionChangeRequest changeRequest;
    private List<Attachment> tempAttachment = new ArrayList<>();

    interface IssueNameWidgetUiBinder extends UiBinder<HTMLPanel, IssueNameDescriptionEditWidget> {
    }

    private static IssueNameWidgetUiBinder ourUiBinder = GWT.create( IssueNameWidgetUiBinder.class );
}