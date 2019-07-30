package ru.protei.portal.ui.documenttype.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.document.doccategory.DocumentCategorySelector;
import ru.protei.portal.ui.documenttype.client.activity.preview.AbstractDocumentTypePreviewActivity;
import ru.protei.portal.ui.documenttype.client.activity.preview.AbstractDocumentTypePreviewView;

/**
 * Вид превью проекта
 */
public class DocumentTypePreviewView extends Composite implements AbstractDocumentTypePreviewView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractDocumentTypePreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<String> shortName() {
        return shortName;
    }

    @Override
    public HasValue<En_DocumentCategory> category() {
        return documentCategory;
    }

    @Override
    public HasValue<String> gost() {
        return gost;
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
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

    @Inject
    @UiField
    Lang lang;
    @UiField
    TextBox shortName;
    @UiField
    TextBox name;
    @UiField
    TextBox gost;
    @Inject
    @UiField(provided = true)
    DocumentCategorySelector documentCategory;

    @Inject
    FixedPositioner positioner;

    private AbstractDocumentTypePreviewActivity activity;

    interface DocumentTypePreviewViewUiBinder extends UiBinder<HTMLPanel, DocumentTypePreviewView> { }
    private static DocumentTypePreviewViewUiBinder ourUiBinder = GWT.create(DocumentTypePreviewViewUiBinder.class);
}