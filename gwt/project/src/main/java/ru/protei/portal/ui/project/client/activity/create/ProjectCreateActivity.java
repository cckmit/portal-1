package ru.protei.portal.ui.project.client.activity.create;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.struct.ProjectInfo;

import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class ProjectCreateActivity implements AbstractProjectCreateActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ProjectEvents.Create event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        fillView();
    }

    @Event
    public void onProductCreated(ProductEvents.QuickCreated event) {
        view.createProductContainer().clear();
    }

        @Override
    public void onSaveClicked() {

        if (!validate()) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        ProjectInfo project = fillProject();
        regionService.createProject(project, new FluentCallback<Long>()
                .withErrorMessage(lang.errNotSaved())
                .withSuccess(aLong -> {
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ProjectEvents.Created());
                }));
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new ProjectEvents.Canceled());
    }

    @Override
    public void onCreateProductClicked() {
        fireEvent(new ProductEvents.QuickCreate(view.createProductContainer()));
    }

    private void fillView() {
        view.name().setValue("");
        view.description().setValue("");
        view.company().setValue(null);
        view.customerType().setValue(null);
        view.products().setValue(null);
    }

    private ProjectInfo fillProject() {
        ProjectInfo project = new ProjectInfo();
        project.setName(view.name().getValue());
        project.setDescription(view.description().getValue());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setCustomerType(view.customerType().getValue());
        project.setProducts(view.products().getValue());
        return project;
    }

    private boolean validate() {
        return view.nameValidator().isValid();
    }

    @Inject
    AbstractProjectCreateView view;
    @Inject
    RegionControllerAsync regionService;

    @Inject
    Lang lang;
}
