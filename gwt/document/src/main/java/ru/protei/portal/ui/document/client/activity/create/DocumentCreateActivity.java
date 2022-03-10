package ru.protei.portal.ui.document.client.activity.create;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidgetActivity;

public abstract class DocumentCreateActivity implements Activity, AbstractDocumentCreateActivity, WizardWidgetActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setWizardActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(DocumentEvents.Create event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_CREATE)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        fireEvent(new ProjectEvents.Search(view.projectSearchContainer(), true, false).withSeparateFormView());
        fireEvent(new ProjectEvents.QuickCreate(view.projectCreateContainer()));
        fireEvent(new DocumentEvents.CreateFromWizard(view.documentContainer()));
        view.resetWizard();
        onProjectSearchClicked();
        view.createEnabled().setEnabled(policyService.hasPrivilegeFor(En_Privilege.PROJECT_CREATE));
    }

    @Event
    public void onShowCreateWithCreatedProject(DocumentEvents.ProjectCreated event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_CREATE)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        view.setWizardTab(lang.documentCreateHeader());
    }

    @Event
    public void onChangeButtonsEnabled(DocumentEvents.SetButtonsEnabled event) {
        view.setWizardButtonsEnabled(event.isEnabled);
    }

    @Override
    public void onClose() {
        fireEvent(new Back());
    }

    @Override
    public void onDone() {
        fireEvent(new DocumentEvents.Save());
    }

    @Override
    public void onDoExtraAction() {
        fireEvent(new DocumentEvents.SaveAndContinue());
    }

    @Override
    public boolean isExtraActionButtonVisible(String tabName) {
        return lang.documentCreateHeader().equals(tabName);
    }

    @Override
    public String getExtraActionButtonName() {
        return lang.buttonSaveAndContinue();
    }

    @Override
    public void onProjectSearchClicked() {
        view.projectSearchContainerVisibility().setVisible(true);
        view.projectCreateContainerVisibility().setVisible(false);
        view.setProjectSearchActive();
    }

    @Override
    public void onProjectCreateClicked() {
        view.projectSearchContainerVisibility().setVisible(false);
        view.projectCreateContainerVisibility().setVisible(true);
        view.setProjectCreateActive();
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentCreateView view;
    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
}
