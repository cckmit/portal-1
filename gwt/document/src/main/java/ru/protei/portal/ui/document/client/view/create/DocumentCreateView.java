package ru.protei.portal.ui.document.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidget;
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidgetActivity;
import ru.protei.portal.ui.document.client.activity.create.AbstractDocumentCreateActivity;
import ru.protei.portal.ui.document.client.activity.create.AbstractDocumentCreateView;

public class DocumentCreateView extends Composite implements AbstractDocumentCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractDocumentCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setWizardActivity(WizardWidgetActivity activity) {
        wizard.setActivity(activity);
    }

    @Override
    public void resetWizard() {
        wizard.selectFirstTab();
    }

    @Override
    public HasWidgets projectSearchContainer() {
        return projectSearchContainer;
    }

    @Override
    public HasWidgets projectCreateContainer() {
        return projectCreateContainer;
    }

    @Override
    public HasVisibility projectSearchContainerVisibility() {
        return projectSearchContainer;
    }

    @Override
    public HasVisibility projectCreateContainerVisibility() {
        return projectCreateContainer;
    }

    @Override
    public void setProjectSearchActive() {
        btnSearch.removeStyleName("btn-white");
        btnSearch.removeStyleName("btn-primary");
        btnSearch.addStyleName("btn-primary");
        btnCreate.removeStyleName("btn-white");
        btnCreate.removeStyleName("btn-primary");
        btnCreate.addStyleName("btn-white");
    }

    @Override
    public void setProjectCreateActive() {
        btnCreate.removeStyleName("btn-white");
        btnCreate.removeStyleName("btn-primary");
        btnCreate.addStyleName("btn-primary");
        btnSearch.removeStyleName("btn-white");
        btnSearch.removeStyleName("btn-primary");
        btnSearch.addStyleName("btn-white");
    }

    @Override
    public HasWidgets documentContainer() {
        return documentContainer;
    }

    private void ensureDebugIds() {}

    @UiHandler("btnSearch")
    public void btnSearchClick(ClickEvent event) {
        if (activity != null) {
            activity.onProjectSearchClicked();
        }
    }

    @UiHandler("btnCreate")
    public void btnCreateClick(ClickEvent event) {
        if (activity != null) {
            activity.onProjectCreateClicked();
        }
    }

    @UiField
    WizardWidget wizard;
    @UiField
    HTMLPanel projectChooseContainer;
    @UiField
    Button btnSearch;
    @UiField
    Button btnCreate;
    @UiField
    HTMLPanel projectSearchContainer;
    @UiField
    HTMLPanel projectCreateContainer;
    @UiField
    HTMLPanel documentContainer;

    private AbstractDocumentCreateActivity activity;

    interface DocumentCreateViewUiBinder extends UiBinder<HTMLPanel, DocumentCreateView> {}
    private static DocumentCreateViewUiBinder ourUiBinder = GWT.create(DocumentCreateViewUiBinder.class);
}
