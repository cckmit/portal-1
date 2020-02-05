package ru.protei.portal.ui.document.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.document.uploader.AbstractDocumentUploader;
import ru.protei.portal.ui.common.client.widget.document.uploader.DocumentUploader;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewActivity;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewView;

import java.util.Date;

public class DocumentPreviewView extends Composite implements AbstractDocumentPreviewView {

    @Inject
    public void onInit() {
        initWidget(uiBinder.createAndBindUi(this));
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

    @Inject
    @UiField
    Lang lang;

    AbstractDocumentPreviewActivity activity;

    interface Binder extends UiBinder<HTMLPanel, DocumentPreviewView> {}
    private final Binder uiBinder = GWT.create(Binder.class);
}
