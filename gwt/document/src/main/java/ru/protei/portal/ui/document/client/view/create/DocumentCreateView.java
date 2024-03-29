package ru.protei.portal.ui.document.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidget;
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidgetActivity;
import ru.protei.portal.ui.common.client.widget.wizard.pane.WizardWidgetPane;
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
    public void setWizardButtonsEnabled(boolean isEnabled) {
        wizard.setButtonsEnabled(isEnabled);
    }

    @Override
    public void resetWizard() {
        wizard.selectFirstTab();
        wizard.setButtonsEnabled(true);
    }

    @Override
    public void setWizardTab(String tabName) {
        wizard.selectTab(tabName);
        wizard.setButtonsEnabled(true);
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

    @Override
    public HasEnabled createEnabled() {
        return btnCreate;
    }

    private void ensureDebugIds() {
        wizard.setTabNameDebugId(documentCreate.getTabName(), DebugIds.DOCUMENT.CREATE.BUTTON);
        wizard.setTabNameDebugId(documentProject.getTabName(), DebugIds.DOCUMENT.PROJECT_SET.BUTTON);
        wizard.setButtonNextDebugId(DebugIds.DOCUMENT.CREATE.NEXT_BUTTON);
        wizard.setButtonPreviousDebugId(DebugIds.DOCUMENT.CREATE.PREVIOUS_BUTTON);
        btnSearch.ensureDebugId(DebugIds.DOCUMENT.PROJECT_SEARCH.BUTTON);
        btnCreate.ensureDebugId(DebugIds.DOCUMENT.PROJECT_CREATE.BUTTON);
    }

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
    WizardWidgetPane documentProject;
    @UiField
    WizardWidgetPane documentCreate;
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
