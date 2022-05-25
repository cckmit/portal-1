package ru.protei.portal.ui.sitefolder.client.view.platform.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractPlatformPreviewActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.preview.AbstractPlatformPreviewView;

public class PlatformPreviewView extends Composite implements AbstractPlatformPreviewView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        attachmentContainer.setHiddenControls(true);

        copyPreviewLink.getElement().setAttribute( "title", lang.siteFolderCopyPreviewLink() );
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractPlatformPreviewActivity activity) {
        this.activity = activity;
        attachmentContainer.setActivity(activity);
    }

    @Override
    public HasVisibility footerContainerVisibility(){
        return footerContainer;
    }

    @Override
    public void setName(String value) {
        name.setText(value);
    }

    @Override
    public void setNameHref(String link) {
        this.name.setHref(link);
    }

    @Override
    public void setCompany(String value) {
        company.setInnerText(value);
    }

    @Override
    public void setManager(String value) {
        manager.setInnerText(value);
    }

    @Override
    public void setParameters(String value) {
        parameters.setInnerText(value);
    }

    @Override
    public void setTechnicalSupportValidity(String technicalSupportValidity) {
        this.technicalSupportValidity.setInnerText(technicalSupportValidity);
    }

    @Override
    public void setProject(String value, String link) {
        project.setText(value);
        project.setHref(link);
    }

    @Override
    public void setComment(String value) {
        comment.setInnerHTML(value);
    }

    @Override
    public HasWidgets contactsContainer() {
        return contactsContainer;
    }

    @Override
    public HasWidgets serversContainer() {
        return serversContainer;
    }

    @Override
    public HasAttachments attachmentsContainer() {
        return attachmentContainer;
    }

    @Override
    public void isFullScreen(boolean isFullScreen) {
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @Override
    public Element getPreviewWrapperContainerElement() {
        return previewWrapperContainer.getElement();
    }

    @UiHandler("openServersButton")
    public void openButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOpenServersClicked();
        }
    }

    @UiHandler("exportServersButton")
    public void exportButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onExportServersClicked();
        }
    }

    @UiHandler("name")
    public void fullScreenClick(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onFullScreenClicked();
        }
    }

    @UiHandler( "backButton" )
    public void onGoToIssuesClicked ( ClickEvent event) {
        if ( activity != null ) {
            activity.onGoToIssuesClicked();
        }
    }

    @UiHandler("copyPreviewLink")
    public void onCopyPreviewLinkClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onCopyPreviewLinkClicked();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        copyPreviewLink.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.COPY_PREVIEW_LINK_BUTTON);
        name.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.NAME);
        openServersButton.ensureDebugId(DebugIds.SITE_FOLDER.SERVER.OPEN_BUTTON);
        exportServersButton.ensureDebugId(DebugIds.SITE_FOLDER.SERVER.EXPORT_BUTTON);
        comment.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.SITE_FOLDER.PLATFORM.COMMENT);
        company.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.SITE_FOLDER.PLATFORM.COMPANY);
        manager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.SITE_FOLDER.PLATFORM.MANAGER);
        parameters.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.SITE_FOLDER.PLATFORM.PARAMETERS);
        project.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.PROJECT);
        technicalSupportValidity.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.SITE_FOLDER.PLATFORM.TECHNICAL_SUPPORT_VALIDITY);
        attachmentContainer.setEnsureDebugId(DebugIds.SITE_FOLDER.PLATFORM.ATTACHMENTS);
        tabWidget.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.TABS);
        tabWidget.setTabNameDebugId(lang.siteFolderServers(), DebugIds.SITE_FOLDER.PLATFORM.TAB_SERVERS);
        tabWidget.setTabNameDebugId(lang.siteFolderCompanyContacts(), DebugIds.SITE_FOLDER.PLATFORM.TAB_COMPANY_CONTACTS);
        serversContainer.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.SERVERS);
        contactsContainer.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.CONTACTS);
    }

    @UiField
    Anchor copyPreviewLink;
    @UiField
    Anchor name;
    @UiField
    SpanElement company;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement parameters;
    @UiField
    Anchor project;
    @UiField
    SpanElement technicalSupportValidity;
    @UiField
    SpanElement comment;
    @UiField
    TabWidget tabWidget;
    @UiField
    HTMLPanel contactsContainer;
    @UiField
    HTMLPanel serversContainer;
    @UiField
    Button openServersButton;
    @UiField
    Button exportServersButton;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentContainer;
    @UiField
    Button backButton;
    @UiField
    HTMLPanel footerContainer;
    @UiField
    HTMLPanel previewWrapperContainer;
    @UiField
    Lang lang;

    private AbstractPlatformPreviewActivity activity;

    interface SiteFolderPreviewViewUiBinder extends UiBinder<HTMLPanel, PlatformPreviewView> {}
    private static SiteFolderPreviewViewUiBinder ourUiBinder = GWT.create(SiteFolderPreviewViewUiBinder.class);
}
