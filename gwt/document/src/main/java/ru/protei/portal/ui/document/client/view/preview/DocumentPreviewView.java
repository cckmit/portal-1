package ru.protei.portal.ui.document.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewActivity;
import ru.protei.portal.ui.document.client.activity.preview.AbstractDocumentPreviewView;

public class DocumentPreviewView extends Composite implements AbstractDocumentPreviewView {

    public DocumentPreviewView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
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
    public void setCreatedDate(String created) {
        this.created.setInnerText(created);
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
    public void setDownloadLink(String link) {
        downloadButton.setHref(link);
    }

    @Override
    public void setExecutionType(String executionType) {
        this.executionType.setInnerText(executionType);
    }


    @UiField Anchor downloadButton;
    @UiField
    HeadingElement header;
    @UiField
    Element version;
    @UiField
    Element created;
    @UiField SpanElement type;
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

    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;

    AbstractDocumentPreviewActivity activity;

    interface Binder extends UiBinder<HTMLPanel, DocumentPreviewView> {}
    private final Binder uiBinder = GWT.create(Binder.class);
}
