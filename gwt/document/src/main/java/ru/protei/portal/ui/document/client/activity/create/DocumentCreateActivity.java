package ru.protei.portal.ui.document.client.activity.create;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidgetActivity;

import java.util.Objects;

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
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        fireEvent(new ProjectEvents.Search(view.projectSearchContainer()));
        fireEvent(new ProjectEvents.QuickCreate(view.projectCreateContainer()));
        fireEvent(new DocumentEvents.Form.Show(view.documentContainer(), new Document(), TAG));
        onProjectSearchClicked();
    }

    @Event
    public void onSetProject(ProjectEvents.Set event) {
        fireEvent(new DocumentEvents.Form.SetProject(event.project, TAG));
    }

    @Override
    public void onClose() {
        fireEvent(new Back());
    }

    @Override
    public void onDone() {
        fireEvent(new DocumentEvents.Form.Save(TAG));
    }

    @Event
    public void onSaved(DocumentEvents.Form.Saved event) {
        if (!Objects.equals(TAG, event.tag)) {
            return;
        }
        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
        fireEvent(new DocumentEvents.ChangeModel());
        fireEvent(new Back());
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

    private AppEvents.InitDetails initDetails;
    private final String TAG = "DocumentCreateActivity";
}
