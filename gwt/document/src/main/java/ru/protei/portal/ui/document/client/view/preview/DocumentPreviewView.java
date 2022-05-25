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

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

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
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber.setText(documentNumber);
    }

    @Override
    public void setDocumentNumberHref(String link) {
        this.documentNumber.setHref(link);
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
            return;
        }
        downloadApprovalSheetButton.setVisible(true);
        downloadApprovalSheetButton.setHref(link);
    }

    @Override
    public void setExecutionType(String executionType) {
        this.executionType.setInnerText(executionType);
    }

    @Override
    public void setApprovedBy(String approvedBy) {
        this.approvedBy.setInnerText(approvedBy);
    }

    @Override
    public void setApprovalDate(String approvalDate) {
        this.approvalDate.setInnerText(approvalDate);
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
    public HasVisibility approvalContainerVisibility() {
        return approvalContainer;
    }

    @Override
    public HasVisibility documentDocUploadContainerLoading() {
        return new HasVisibility() { // Because documentDocUploadContainerLoading has 'd-flex' class with !important display
            public boolean isVisible() { return !documentDocUploadContainerLoading.hasClassName(HIDE); }
            public void setVisible(boolean visible) {
                documentDocUploadContainerLoading.removeClassName(HIDE);
                if (!visible) documentDocUploadContainerLoading.addClassName(HIDE);
            }
        };
    }

    @Override
    public void showFullScreen(boolean isShowFullScreen) {
        footerContainer.setVisible(isShowFullScreen);
        preview.setStyleName("card-with-fixable-footer", isShowFullScreen);
    }

    @UiHandler("documentNumber")
    public void onDocumentNumberClicked(ClickEvent event) {
        event.preventDefault();

        activity.onFullScreenClicked();
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
        downloadDocButton.ensureDebugId(DebugIds.DOCUMENT.PREVIEW.DOWNLOAD_DOC_BUTTON);
        downloadPdfButton.ensureDebugId(DebugIds.DOCUMENT.PREVIEW.DOWNLOAD_PDF_BUTTON);
        downloadApprovalSheetButton.ensureDebugId(DebugIds.DOCUMENT.PREVIEW.DOWNLOAD_APPROVAL_BUTTON);
        documentDocComment.ensureDebugId(DebugIds.DOCUMENT.PREVIEW.DOC_COMMENT_INPUT);
        documentDocUploader.ensureDebugId(DebugIds.DOCUMENT.PREVIEW.DOC_UPLOADER);
        uploadDocFile.ensureDebugId(DebugIds.DOCUMENT.PREVIEW.UPLOAD_DOC_FILE_BUTTON);
        backButton.ensureDebugId(DebugIds.DOCUMENT.PREVIEW.BACK_BUTTON);

        documentNumber.ensureDebugId(DebugIds.DOCUMENT.PREVIEW.HEADER_LABEL);
        createdBy.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.CREATED_BY);
        annotation.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.ANNOTATION_LABEL);
        keyWordsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.KEY_WORDS_LABEL);
        keyWords.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.KEY_WORDS);
        commonHeaderLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.COMMON_HEADER_LABEL);
        versionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.VERSION_LABEL);
        version.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.VERSION);
        typeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.TYPE_LABEL);
        type.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.TYPE);
        executionTypeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.EXECUTION_TYPE_LABEL);
        executionType.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.EXECUTION_TYPE);
        projectLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.PROJECT_LABEL);
        project.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.PROJECT);
        numberDecimalLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.NUMBER_DECIMAL_LABEL);
        numberDecimal.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.NUMBER_DECIMAL);
        numberInventoryLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.NUMBER_INVENTORY_LABEL);
        numberInventory.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.NUMBER_INVENTORY);
        workGroupHeaderLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.WORK_GROUP_HEADER_LABEL);
        managerLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.MANAGER_LABEL);
        manager.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.MANAGER);
        registrarLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.REGISTRAR_LABEL);
        registrar.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.REGISTRAR);
        contractorLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.CONTRACTOR_LABEL);
        contractor.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.CONTRACTOR);
        membersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.MEMBERS_LABEL);
        members.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.MEMBERS);
        memberUploadWorkDocumentationLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.UPLOAD_WORK_DOCUMENTATION_LABEL);
        documentDocCommentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.DOC_COMMENT_LABEL);
        documentDocUploadContainerLoading.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.DOCUMENT.PREVIEW.DOC_UPLOAD_CONTAINER_LOADING);
    }


    @UiField
    HTMLPanel preview;
    @UiField
    Anchor downloadPdfButton;
    @UiField
    Anchor downloadDocButton;
    @UiField
    Anchor downloadApprovalSheetButton;
    @UiField
    Anchor documentNumber;
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
    SpanElement approvedBy;
    @UiField
    SpanElement approvalDate;
    @UiField
    HTMLPanel footerContainer;
    @UiField
    Button backButton;
    @UiField
    HTMLPanel documentDocUploadContainer;
    @UiField
    HTMLPanel approvalContainer;
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

    private AbstractDocumentPreviewActivity activity;

    interface Binder extends UiBinder<HTMLPanel, DocumentPreviewView> {}
    private final Binder uiBinder = GWT.create(Binder.class);
}
