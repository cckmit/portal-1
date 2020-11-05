package ru.protei.portal.ui.sitefolder.client.view.platform.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.attachment.list.AttachmentList;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;
import ru.protei.portal.ui.common.client.widget.attachment.list.events.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectButtonSelector;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.AbstractPlatformEditActivity;
import ru.protei.portal.ui.sitefolder.client.activity.plaform.edit.AbstractPlatformEditView;

public class PlatformEditView extends Composite implements AbstractPlatformEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setPlatformIndependentProjects(Boolean platformIndependentProjects) {
        project.setPlatformIndependentProject(platformIndependentProjects);
    }

    @Override
    public void setActivity(AbstractPlatformEditActivity activity) {
        this.activity = activity;
        attachmentContainer.setActivity(activity);
    }

    @Override
    public void setFileUploadHandler(AttachmentUploader.FileUploadHandler handler) {
        fileUploader.setUploadHandler(handler);
    }

    @Override
    public void setCaseNumber(Long caseNumber) {
        fileUploader.autoBindingToCase(En_CaseType.SF_PLATFORM, caseNumber);
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<EntityOption> project() {
        return project;
    }

    @Override
    public HasValue<PersonShortView> manager() {
        return manager;
    }

    @Override
    public HasValue<String> parameters() {
        return parameters;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasWidgets listContainer() {
        return listContainer;
    }

    @Override
    public HasVisibility listContainerVisibility() {
        return listContainer;
    }

    @Override
    public HasVisibility listContainerHeaderVisibility() {
        return listContainerHeader;
    }

    @Override
    public HasEnabled companyEnabled() {
        return company;
    }

    @Override
    public HasEnabled managerEnabled() {
        return manager;
    }

    @Override
    public HasValidable nameValidator() {
        return name;
    }

    @Override
    public HasValidable companyValidator() {
        return company;
    }

    @Override
    public HasVisibility openButtonVisibility() {
        return openButton;
    }

    @Override
    public HasVisibility createButtonVisibility() {
        return createButton;
    }

    @Override
    public HasWidgets contactsContainer() {
        return contactsContainer;
    }

    @Override
    public HasAttachments attachmentsContainer() {
        return attachmentContainer;
    }

    @UiHandler("saveButton")
    public void saveButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void cancelButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    @UiHandler("openButton")
    public void openButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onOpenClicked();
        }
    }

    @UiHandler("createButton")
    public void createButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onCreateClicked();
        }
    }

    @UiHandler("company")
    public void onCompanySelected(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onCompanySelected();
        }
    }

    @UiHandler("attachmentContainer")
    public void attachmentContainerRemove(RemoveEvent event) {
        if (activity != null) {
            activity.onRemoveAttachment(event.getAttachment());
        }
    }

    @UiHandler("project")
    public void onProjectSelected(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.refreshProjectSpecificFields();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        project.setEnsureDebugId(DebugIds.SITE_FOLDER.PLATFORM.PROJECT);
        company.setEnsureDebugId(DebugIds.SITE_FOLDER.PLATFORM.COMPANY);
        name.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.NAME);
        manager.setEnsureDebugId(DebugIds.SITE_FOLDER.PLATFORM.MANAGER);
        parameters.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.PARAMETERS);
        comment.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.COMMENT);
        fileUploader.setEnsureDebugId(DebugIds.SITE_FOLDER.PLATFORM.UPLOADER);
        attachmentContainer.setEnsureDebugId(DebugIds.SITE_FOLDER.PLATFORM.ATTACHMENTS);
        tabWidget.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.TABS);
        tabWidget.setTabNameDebugId(lang.siteFolderServers(), DebugIds.SITE_FOLDER.PLATFORM.TAB_SERVERS);
        tabWidget.setTabNameDebugId(lang.siteFolderCompanyContacts(), DebugIds.SITE_FOLDER.PLATFORM.TAB_COMPANY_CONTACTS);
        listContainer.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.SERVERS);
        openButton.ensureDebugId(DebugIds.SITE_FOLDER.SERVER.OPEN_BUTTON);
        createButton.ensureDebugId(DebugIds.SITE_FOLDER.SERVER.CREATE_BUTTON);

        contactsContainer.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.CONTACTS);

        saveButton.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.SITE_FOLDER.PLATFORM.CANCEL_BUTTON);
    }

    @UiField
    ValidableTextBox name;
    @Inject
    @UiField(provided = true)
    CompanySelector company;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;
    @UiField
    TextArea parameters;
    @UiField
    TextArea comment;
    @UiField
    HTMLPanel listContainerHeader;
    @UiField
    TabWidget tabWidget;
    @UiField
    HTMLPanel listContainer;
    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @UiField
    Button createButton;
    @UiField
    Button openButton;
    @UiField
    HTMLPanel contactsContainer;
    @UiField
    AttachmentUploader fileUploader;
    @Inject
    @UiField(provided = true)
    AttachmentList attachmentContainer;
    @Inject
    @UiField(provided = true)
    ProjectButtonSelector project;

    @UiField
    Lang lang;

    private AbstractPlatformEditActivity activity;

    interface SiteFolderEditViewUiBinder extends UiBinder<HTMLPanel, PlatformEditView> {}
    private static SiteFolderEditViewUiBinder ourUiBinder = GWT.create(SiteFolderEditViewUiBinder.class);
}
