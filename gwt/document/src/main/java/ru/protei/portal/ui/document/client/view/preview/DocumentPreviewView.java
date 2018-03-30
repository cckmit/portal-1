package ru.protei.portal.ui.document.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
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
    public void setName(String name) {
        this.name.setInnerText(name);
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

    @UiField LegendElement header;
    @UiField SpanElement name;
    @UiField SpanElement created;
    @UiField SpanElement type;
    @UiField SpanElement annotation;
    @UiField SpanElement project;
    @UiField SpanElement manager;
    @UiField SpanElement numberDecimal;
    @UiField SpanElement numberInventory;
    @UiField SpanElement keyWords;

    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;

    AbstractDocumentPreviewActivity activity;

    interface Binder extends UiBinder<HTMLPanel, DocumentPreviewView> {}
    private final Binder uiBinder = GWT.create(Binder.class);
}