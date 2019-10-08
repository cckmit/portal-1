package ru.protei.portal.ui.document.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditActivity;
import ru.protei.portal.ui.document.client.activity.edit.AbstractDocumentEditView;

public class DocumentEditView extends Composite implements AbstractDocumentEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDocumentEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets documentContainer() {
        return documentContainer;
    }

    private void ensureDebugIds() {}

    @UiHandler("saveBtn")
    public void saveBtnClick(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelBtn")
    public void cancelBtnClick(ClickEvent event) {
        if (activity != null) {
            activity.onCloseClicked();
        }
    }

    @UiField
    HTMLPanel documentContainer;
    @UiField
    Button saveBtn;
    @UiField
    Button cancelBtn;

    private AbstractDocumentEditActivity activity;

    interface DocumentEditViewUiBinder extends UiBinder<HTMLPanel, DocumentEditView> {}
    private static DocumentEditViewUiBinder ourUiBinder = GWT.create(DocumentEditViewUiBinder.class);
}
