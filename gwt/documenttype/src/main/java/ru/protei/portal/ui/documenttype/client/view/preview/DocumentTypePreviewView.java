package ru.protei.portal.ui.documenttype.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
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

    @UiField
    HTMLPanel preview;

    @Inject
    @UiField
    Lang lang;

    AbstractDocumentTypePreviewActivity activity;

    interface DocumentTypePreviewViewUiBinder extends UiBinder<HTMLPanel, DocumentTypePreviewView> {
    }

    private static DocumentTypePreviewViewUiBinder ourUiBinder = GWT.create(DocumentTypePreviewViewUiBinder.class);
}