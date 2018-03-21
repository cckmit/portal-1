package ru.protei.portal.ui.documentation.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.documentation.client.activity.preview.AbstractDocumentationPreviewActivity;
import ru.protei.portal.ui.documentation.client.activity.preview.AbstractDocumentationPreviewView;

public class DocumentationPreviewView extends Composite implements AbstractDocumentationPreviewView {

    public DocumentationPreviewView() {
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
    public void setActivity(AbstractDocumentationPreviewActivity activity) {
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

    @Override
    public void setCopyBtnEnabledStyle(boolean isEnabled) {
        if (isEnabled) {
            copy.removeStyleName("link-disabled");
        } else {
            copy.addStyleName("link-disabled");
        }
    }

    @Override
    public void setRemoveBtnEnabledStyle(boolean isEnabled) {
        if (isEnabled) {
            remove.removeStyleName("link-disabled");
        } else {
            remove.addStyleName("link-disabled");
        }
    }

    @UiHandler("copy")
    public void onCopyClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCopyClicked();
        }
    }

    @UiHandler("remove")
    public void onRemoveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onRemoveClicked();
        }
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
    @UiField Button copy;
    @UiField Button remove;

    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;

    AbstractDocumentationPreviewActivity activity;

    interface Binder extends UiBinder<HTMLPanel, DocumentationPreviewView> {}
    private final Binder uiBinder = GWT.create(Binder.class);
}
