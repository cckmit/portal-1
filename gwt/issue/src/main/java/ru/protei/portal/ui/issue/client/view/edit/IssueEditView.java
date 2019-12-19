package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Вид создания и редактирования обращения
 */
public class IssueEditView extends Composite implements AbstractIssueEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();

        copyNumber.getElement().setAttribute( "title", lang.issueCopyNumber() );
    }

    @Override
    public void setActivity( AbstractIssueEditActivity activity ) {
        this.activity = activity;
        nameWidget.setActivity( activity );
    }

    @Override
    public void setCaseNumber( Long caseNumber ) {
        number.setText( lang.crmPrefix() + caseNumber );
    }

    @Override
    public void setName( String issueName ) {
        nameWidget.setName( issueName );
    }

    @Override
    public void setNameVisible( boolean isNameVisible ) {
        nameWidget.setVisible( isNameVisible );
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaEditContainer;
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public HasWidgets getInfoContainer() {
        return issueInfoContainer;
    }

    @Override
    public void setPrivateIssue( boolean isPrivate ) {
        if (isPrivate) {
            privacyIcon.setClassName( "fas fa-lock text-danger m-l-10" );
            privacyIcon.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PRIVATE );
        } else {
            privacyIcon.setClassName( "fas fa-unlock text-success m-l-10" );
            privacyIcon.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PUBLIC );
        }
    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @Override
    public HasVisibility nameAndDescriptionEditButtonVisibility() {
        return nameAndDescriptionEditButton;
    }

    @UiHandler("copyNumber")
    public void onCopyNumberClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onCopyNumberClicked();
        }
    }

    @UiHandler("nameAndDescriptionEditButton")
    public void onEditNameAndDescriptionButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onNameAndDescriptionEditClicked(this);
        }
    }

    @UiHandler( "number" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiHandler("backButton")
    public void onBackButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        privacyIcon.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.PRIVACY_ICON);
        number.ensureDebugId(DebugIds.ISSUE_PREVIEW.FULL_SCREEN_BUTTON);
        copyNumber.ensureDebugId(DebugIds.ISSUE.COPY_NUMBER_BUTTON);
    }

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;
    @UiField
    Anchor copyNumber;
    @UiField
    Anchor number;
    @UiField
    Element createdBy;
    @UiField
    HTMLPanel numberPanel;
    @UiField
    Element privacyIcon;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    HTMLPanel metaEditContainer;
    @UiField
    Button nameAndDescriptionEditButton;
    @UiField
    HTMLPanel cardBody;
    @UiField
    HTMLPanel issueInfoContainer;
    @UiField
    Button backButton;
    @Inject
    @UiField(provided = true)
    IssueNameWidget nameWidget;
    @UiField
    HTMLPanel linksContainer;

    private AbstractIssueEditActivity activity;


    interface IssueEditViewUiBinder extends UiBinder<HTMLPanel, IssueEditView> {}
    private static IssueEditViewUiBinder ourUiBinder = GWT.create(IssueEditViewUiBinder.class);
}