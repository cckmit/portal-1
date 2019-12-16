package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.common.client.widget.makdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.dict.En_Privilege.ISSUE_EDIT;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;
import static ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView.DESCRIPTION;


public class IssueNameInfoWidget extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
        description.setRenderer( ( text, consumer ) -> renderMarkupText( text, En_TextMarkup.MARKDOWN, consumer ) );
        description.setDisplayPreviewHandler( isDisplay -> onDisplayPreviewChanged( DESCRIPTION, isDisplay ) );
        copyNumberAndName.getElement().setAttribute( "title", lang.issueCopyNumberAndName() );
    }

    public void setActivity( AbstractIssueNameWidgetActivity activity ) {
        this.activity = activity;
    }

    public void setIssue( CaseObject issue ) {
        this.issue = issue;
        toReadOnlyMode();
        setNameRO( issue.getName() == null ? "" : issue.getName(), En_ExtAppType.JIRA.getCode().equals( issue.getExtAppType() ) ? issue.getJiraUrl() : "" );
        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup( issue );
        renderMarkupText( issue.getInfo(), textMarkup, converted -> setDescriptionRO( converted ) );
    }

    public void edit() {
        setDescriptionPreviewAllowed( makePreviewDisplaying( AbstractIssueEditView.DESCRIPTION ) );
        toEditMode();
        name.setValue( issue.getName() );
        description.setValue( issue.getInfo() );
    }

    @UiHandler("copyNumberAndName")
    public void onCopyNumberAndNameClick( ClickEvent event ) {
        event.preventDefault();
        boolean isCopied = ClipboardUtils.copyToClipboard( lang.crmPrefix() + issue.getCaseNumber() + " " + issue.getName() );

        if (isCopied) {
            activity.fireEvent( new NotifyEvents.Show( lang.issueCopiedToClipboard(), NotifyEvents.NotifyType.SUCCESS ) );
        } else {
            activity.fireEvent( new NotifyEvents.Show( lang.errCopyToClipboard(), NotifyEvents.NotifyType.ERROR ) );
        }
    }

    @UiHandler("saveNameAndDescriptionButton")
    public void onSaveNameAndDescriptionButtonClick( ClickEvent event ) {
        onSaveNameAndDescriptionClicked();
    }

    @UiHandler("cancelNameAndDescriptionButton")
    public void onCancelNameAndDescriptionButtonClick( ClickEvent event ) {
        toReadOnlyMode();
    }

    private void toReadOnlyMode() {
        nameAndDescriptionButtonsPanel.addClassName( HIDE );
        descriptionContainer.setVisible( false );
        nameContainer.setVisible( false );

        nameROLabel.removeClassName( HIDE );
        descriptionRO.removeClassName( HIDE );
        copyNumberAndName.setVisible( true );
    }

    private void toEditMode() {
        nameAndDescriptionButtonsPanel.removeClassName( HIDE );
        descriptionContainer.setVisible( true );
        nameContainer.setVisible( true );

        nameROLabel.addClassName( HIDE );
        descriptionRO.addClassName( HIDE );
        copyNumberAndName.setVisible( false );
    }

    private void onSaveNameAndDescriptionClicked() {
        if (!nameValidator.isValid()) {
            activity.fireEvent( new NotifyEvents.Show( lang.errEmptyName(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }
        if (requested) return;
        requested = true;

        final CaseNameAndDescriptionChangeRequest changeRequest = new CaseNameAndDescriptionChangeRequest(
                issue.getId(), name.getValue(), description.getValue() );

        issueService.saveIssueNameAndDescription( changeRequest, new FluentCallback<Void>()
                .withError( t -> requested = false )
                .withSuccess( result -> {
                    requested = false;

                    issue.setName( changeRequest.getName() );
                    issue.setInfo( changeRequest.getInfo() );

                    setIssue( issue );

                    activity.fireEvent( new NotifyEvents.Show( lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS ) );
                    activity.onIssueNameInfoChanged( issue );
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

    private void setNameRO( String value, String jiraUrl ) {
        if (jiraUrl.isEmpty() || !value.startsWith( "CLM" )) {
            this.nameROLabel.setInnerHTML( value );
        } else {
            String idCLM = value.split( " " )[0];
            String remainingName = "&nbsp;" + value.substring( idCLM.length() );

            AnchorElement jiraLink = DOM.createAnchor().cast();

            jiraLink.setHref( jiraUrl + idCLM );
            jiraLink.setTarget( "_blank" );
            jiraLink.setInnerText( idCLM );

            LabelElement nameWithoutLink = DOM.createLabel().cast();
            nameWithoutLink.setInnerHTML( remainingName );

            this.nameROLabel.setInnerHTML( "" );
            this.nameROLabel.appendChild( jiraLink );
            this.nameROLabel.appendChild( nameWithoutLink );
        }
    }

    private void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( ISSUE_EDIT + "_" + key, String.valueOf( isDisplay ) );
    }

    private void setDescriptionRO( String value ) {
        descriptionRO.setInnerHTML( value );
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
        nameROLabel.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.NAME_FIELD );
        description.setEnsureDebugId( DebugIds.ISSUE.DESCRIPTION_INPUT );
        descriptionRO.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.DESCRIPTION_FIELD );
        copyNumberAndName.ensureDebugId( DebugIds.ISSUE.COPY_NUMBER_AND_NAME_BUTTON );
        nameLabel.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME );
        descriptionLabel.setId( DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO );
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
    HeadingElement nameROContainer;
    @UiField
    HTMLPanel nameContainer;
    @UiField
    LabelElement nameLabel;

    @UiField
    LabelElement nameROLabel;
    @UiField
    Anchor copyNumberAndName;
    @UiField
    DivElement descriptionRO;
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
    private AbstractIssueNameWidgetActivity activity;
    private boolean requested;
    private CaseObject issue;

    interface IssueNameWidgetUiBinder extends UiBinder<HTMLPanel, IssueNameInfoWidget> {
    }

    private static IssueNameWidgetUiBinder ourUiBinder = GWT.create( IssueNameWidgetUiBinder.class );
}