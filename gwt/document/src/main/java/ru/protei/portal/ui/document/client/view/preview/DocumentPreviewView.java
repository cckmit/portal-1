package ru.protei.portal.ui.document.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;
import ru.protei.portal.ui.common.client.widget.document.uploader.DocumentUploader;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewActivity;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewView;

public class DocumentPreviewView extends Composite implements AbstractDocumentPreviewView {

    @Inject
    public void onInit() {
        initWidget(uiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDocumentPreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setHeader(String header) {
        this.header.setInnerText(header);
    }

    @Override
    public void setVersion(String text) {
        this.version.setInnerText(text);
    }

    @Override
    public void setCreatedBy(String created) {
        this.createdBy.setInnerHTML(created);
    }

    @Override
    public void setType(String type) {
        this.type.setInnerText(type);
    }

    @Override
    public void setAnnotation(String annotation) {
        this.annotation.setInnerText(annotation);
    }

    @Override
    public void setProject(String project) {
        this.project.setInnerText(project);
    }

    @Override
    public void setManager(String manager) {
        this.manager.setInnerText(manager);
    }

    @Override
    public void setRegistrar(String text) {
        this.registrar.setInnerText(text);
    }

    @Override
    public void setContractor(String text) {
        this.contractor.setInnerText(text);
    }

    @Override
    public void setMembers(String text) {
        this.members.setInnerText(text);
    }

    @Override
    public void setNumberDecimal(String numberDecimal) {
        this.numberDecimal.setInnerText(numberDecimal);
    }

    @Override
    public void setNumberInventory(String numberInventory) {
        this.numberInventory.setInnerText(numberInventory);
    }

    @Override
    public void setKeyWords(String keyWords) {
        this.keyWords.setInnerText(keyWords);
    }

    @Override
    public void setDownloadLinkPdf(String link) {
        if (StringUtils.isEmpty(link)) {
            downloadPdfButton.setVisible(false);
            return;
        }
        downloadPdfButton.setVisible(true);
        downloadPdfButton.setHref(link);
    }

    @Override
    public void setDownloadLinkDoc(String link) {
        if (StringUtils.isEmpty(link)) {
            downloadDocButton.setVisible(false);
            return;
        }
        downloadDocButton.setVisible(true);
        downloadDocButton.setHref(link);
    }

    @Override
    public void setDownloadLinkApprovalSheet(String link) {
        if (StringUtils.isEmpty(link)) {
            downloadApprovalSheetButton.setVisible(false);
        }
        downloadApprovalSheetButton.setVisible(true);
        downloadApprovalSheetButton.setHref(link);
    }

    @Override
    public void setExecutionType(String executionType) {
        this.executionType.setInnerText(executionType);
    }

    @Override
    public AbstractDocumentUploader documentDocUploader() {
        return documentDocUploader;
    }

    @Override
    public HasValue<String> documentDocComment() {
        return documentDocComment;
    }

    @Override
    public HasVisibility documentDocVisibility() {
        return documentDocUploadContainer;
    }

    @Override
    public HasVisibility documentDocUploadContainerLoading() {
        return new HasVisibility() { // Because documentDocUploadContainerLoading has 'd-flex' class with !important display
            public boolean isVisible() { return !documentDocUploadContainerLoading.hasClassName("hide"); }
            public void setVisible(boolean visible) {
                documentDocUploadContainerLoading.removeClassName("hide");
                if (!visible) documentDocUploadContainerLoading.addClassName("hide");
            }
        };
    }

    @Override
    public HasVisibility footerVisibility() {
        return footerContainer;
    }

    @UiHandler("backButton")
    public void backButtonClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onBackClicked();
        }
    }

    @UiHandler("uploadDocFile")
    public void uploadDocFileClick(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onUploadDocFileClicked();
        }
    }

    private void ensureDebugIds() {
        downloadDocButton.ensureDebugId(DebugIds.DOCUMENT_PREVIEW.DOWNLOAD_DOC_BUTTON);
        downloadPdfButton.ensureDebugId(DebugIds.DOCUMENT_PREVIEW.DOWNLOAD_PDF_BUTTON);
        downloadApprovalSheetButton.ensureDebugId(DebugIds.DOCUMENT_PREVIEW.DOWNLOAD_APPROVAL_BUTTON);
        documentDocComment.ensureDebugId(DebugIds.DOCUMENT_PREVIEW.DOC_COMMENT_INPUT);
        documentDocUploader.ensureDebugId(DebugIds.DOCUMENT_PREVIEW.DOC_UPLOADER);
        uploadDocFile.ensureDebugId(DebugIds.DOCUMENT_PREVIEW.UPLOAD_DOC_FILE_BUTTON);
        backButton.ensureDebugId(DebugIds.DOCUMENT_PREVIEW.BACK_BUTTON);

        header.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.HEADER_LABEL);
        createdBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.CREATED_BY);
        annotation.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.ANNOTATION_LABEL);
        keyWordsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.KEY_WORDS_LABEL);
        keyWords.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.KEY_WORDS);
        commonHeaderLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.COMMON_HEADER_LABEL);
        versionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.VERSION_LABEL);
        version.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.VERSION);
        typeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.TYPE_LABEL);
        type.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.TYPE);
        executionTypeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.EXECUTION_TYPE_LABEL);
        executionType.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.EXECUTION_TYPE);
        projectLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.PROJECT_LABEL);
        project.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.PROJECT);
        numberDecimalLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.NUMBER_DECIMAL_LABEL);
        numberDecimal.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.NUMBER_DECIMAL);
        numberInventoryLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.NUMBER_INVENTORY_LABEL);
        numberInventory.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.NUMBER_INVENTORY);
        workGroupHeaderLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.WORK_GROUP_HEADER_LABEL);
        managerLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.MANAGER_LABEL);
        manager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.MANAGER);
        registrarLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.REGISTRAR_LABEL);
        registrar.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.REGISTRAR);
        contractorLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.CONTRACTOR_LABEL);
        contractor.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.CONTRACTOR);
        membersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.MEMBERS_LABEL);
        members.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.MEMBERS);
        memberUploadWorkDocumentationLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.UPLOAD_WORK_DOCUMENTATION_LABEL);
        documentDocCommentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.DOC_COMMENT_LABEL);
        documentDocUploadContainerLoading.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT_PREVIEW.DOC_UPLOAD_CONTAINER_LOADING);
    }

    @UiField
    Anchor downloadPdfButton;
    @UiField
    Anchor downloadDocButton;
    @UiField
    Anchor downloadApprovalSheetButton;
    @UiField
    HeadingElement header;
    @UiField
    Element version;
    @UiField
    Element createdBy;
    @UiField
    SpanElement type;
    @UiField
    DivElement annotation;
    @UiField
    SpanElement project;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement registrar;
    @UiField
    SpanElement contractor;
    @UiField
    SpanElement members;
    @UiField
    SpanElement numberDecimal;
    @UiField
    SpanElement numberInventory;
    @UiField
    SpanElement keyWords;
    @UiField
    SpanElement executionType;
    @UiField
    HTMLPanel footerContainer;
    @UiField
    Button backButton;
    @UiField
    HTMLPanel documentDocUploadContainer;
    @UiField
    DivElement documentDocUploadContainerLoading;
    @Inject
    @UiField(provided = true)
    DocumentUploader documentDocUploader;
    @UiField
    TextBox documentDocComment;
    @UiField
    Button uploadDocFile;
    @UiField
    HeadingElement commonHeaderLabel;
    @UiField
    SpanElement keyWordsLabel;
    @UiField
    LabelElement versionLabel;
    @UiField
    LabelElement typeLabel;
    @UiField
    LabelElement executionTypeLabel;
    @UiField
    LabelElement projectLabel;
    @UiField
    LabelElement numberDecimalLabel;
    @UiField
    LabelElement numberInventoryLabel;
    @UiField
    HeadingElement workGroupHeaderLabel;
    @UiField
    LabelElement managerLabel;
    @UiField
    LabelElement registrarLabel;
    @UiField
    LabelElement contractorLabel;
    @UiField
    LabelElement membersLabel;
    @UiField
    HeadingElement memberUploadWorkDocumentationLabel;
    @UiField
    LabelElement documentDocCommentLabel;

    @Inject
    @UiField
    Lang lang;

    AbstractDocumentPreviewActivity activity;

    interface Binder extends UiBinder<HTMLPanel, DocumentPreviewView> {}
    private final Binder uiBinder = GWT.create(Binder.class);
}
