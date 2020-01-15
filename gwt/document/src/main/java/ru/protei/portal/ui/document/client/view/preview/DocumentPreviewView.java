package ru.protei.portal.ui.document.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewActivity;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewView;

public class DocumentPreviewView extends Composite implements AbstractDocumentPreviewView {

    public DocumentPreviewView() {
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
        this.annotation.setText(annotation);
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
        }
        downloadPdfButton.setVisible(true);
        downloadPdfButton.setHref(link);
    }

    @Override
    public void setDownloadLinkDoc(String link) {
        if (StringUtils.isEmpty(link)) {
            downloadDocButton.setVisible(false);
        }
        downloadDocButton.setVisible(true);
        downloadDocButton.setHref(link);
    }

    @Override
    public void setExecutionType(String executionType) {
        this.executionType.setInnerText(executionType);
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

    @UiField
    Anchor downloadPdfButton;
    @UiField
    Anchor downloadDocButton;
    @UiField
    HeadingElement header;
    @UiField
    Element version;
    @UiField
    Element createdBy;
    @UiField
    SpanElement type;
    @UiField
    Label annotation;
    @UiField
    SpanElement project;
    @UiField
    SpanElement manager;
    @UiField
    SpanElement registrar;
    @UiField
    SpanElement contractor;
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

    @Inject
    @UiField
    Lang lang;

    AbstractDocumentPreviewActivity activity;

    interface Binder extends UiBinder<HTMLPanel, DocumentPreviewView> {}
    private final Binder uiBinder = GWT.create(Binder.class);
}
