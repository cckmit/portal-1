package ru.protei.portal.ui.issue.client.view.preview;

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
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;
import ru.protei.portal.ui.issue.client.view.edit.IssueNameWidget;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Вид превью обращения
 */
public class IssuePreviewView extends Composite implements AbstractIssuePreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        copyNumber.getElement().setAttribute("title", lang.issueCopyNumber());
        ensureDebugIds();
    }

    @Override
    public void setActivity( AbstractIssuePreviewActivity activity ) {
        this.activity = activity;
        nameWidget.setActivity( activity );
    }

    @Override
    public void setPrivateIssue( boolean isPrivate ) {
        if ( isPrivate ) {
            privateIssue.setClassName( "fa fa-lock text-danger m-l-10" );
            privateIssue.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PRIVATE);
        } else {
            privateIssue.setClassName( "fa fa-unlock-alt text-success m-l-10"  );
            privateIssue.setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.PRIVACY_ICON_PUBLIC);
        }
    }

    @Override
    public void setCreatedBy(String value) {
        this.createdBy.setInnerHTML( value );
    }

    @Override
    public HasWidgets getInfoContainer() {
        return issueInfoContainer;
    }

    @Override
    public void setCaseNumber(Long caseNumber) {
        number.setText(lang.crmPrefix() + caseNumber);
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
    public HasVisibility backBtnVisibility() {
        return backButtonContainer;
    }

    @Override
    public HasWidgets getTagsContainer() {
        return tagsContainer;
    }

    @Override
    public HasWidgets getMetaContainer() {
        return metaContainer;
    }

    @Override
    public HasWidgets getLinksContainer() {
        return linksContainer;
    }

    @Override
    public void setFullScreen( boolean isFullScreen) {
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
        if (isFullScreen) {
            metaContainer.addStyleName("p-r-15 p-l-15");
        } else {
            metaContainer.removeStyleName("p-r-15 p-l-15");
        }
    }

    @UiHandler( "number" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiHandler( "backButton" )
    public void onGoToIssuesClicked ( ClickEvent event) {
        if ( activity != null ) {
            activity.onGoToIssuesClicked();
        }
    }

    @UiHandler("copyNumber")
    public void onCopyClick(ClickEvent event) {
        event.preventDefault();
        if ( activity != null ) {
            activity.onCopyNumberClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        privateIssue.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.PRIVACY_ICON);
        number.ensureDebugId(DebugIds.ISSUE_PREVIEW.FULL_SCREEN_BUTTON);
        createdBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE_PREVIEW.DATE_CREATED);
        copyNumber.ensureDebugId(DebugIds.ISSUE_PREVIEW.COPY_NUMBER_BUTTON);
    }

    @UiField
    HTMLPanel cardBody;
    @UiField
    Element privateIssue;
    @UiField
    Element createdBy;

    @Inject
    @UiField
    Lang lang;
    @UiField
    Button backButton;
    @UiField
    Anchor number;
    @UiField
    HTMLPanel backButtonContainer;
    @UiField
    Anchor copyNumber;
    @UiField
    HTMLPanel previewWrapperContainer;
    @UiField
    HTMLPanel linksContainer;
    @UiField
    HTMLPanel tagsContainer;
    @UiField
    HTMLPanel metaContainer;
    @UiField
    HTMLPanel issueInfoContainer;
    @Inject
    @UiField(provided = true)
    IssueNameWidget nameWidget;

    AbstractIssuePreviewActivity activity;

    interface IssuePreviewViewUiBinder extends UiBinder<HTMLPanel, IssuePreviewView> {}
    private static IssuePreviewViewUiBinder ourUiBinder = GWT.create( IssuePreviewViewUiBinder.class );
}