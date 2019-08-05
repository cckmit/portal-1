package ru.protei.portal.ui.project.client.activity.create;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.struct.ProjectInfo;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
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
    public void onShow(ProjectEvents.QuickCreate event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        resetView();
    }

    @Override
    public void onSaveClicked() {

        if (!validate()) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        ProjectInfo project = fillProject();
        regionService.saveProject(project, new FluentCallback<ProjectInfo>()
                .withErrorMessage(lang.errNotSaved())
                .withSuccess(projectInfo -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ProjectEvents.Set(projectInfo));
                    resetView();
                }));
    }

    @Override
    public void onResetClicked() {
        resetView();
    }

    private void resetView() {
        view.name().setValue("");
        view.description().setValue("");
        view.region().setValue(null);
        view.direction().setValue(null);
        view.customerType().setValue(null);
        view.company().setValue(null);
        view.products().setValue(null);
    }

    private ProjectInfo fillProject() {
        ProjectInfo project = new ProjectInfo();
        project.setName(view.name().getValue());
        project.setDescription(view.description().getValue());
        project.setRegion(view.region().getValue());
        project.setProductDirection(EntityOption.fromProductDirectionInfo(view.direction().getValue()));
        project.setCustomerType(view.customerType().getValue());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setProducts(view.products().getValue());
        return project;
    }

    private boolean validate() {
        return view.nameValidator().isValid() &&
                view.regionValidator().isValid() &&
                view.directionValidator().isValid() &&
                view.customerTypeValidator().isValid() &&
                view.companyValidator().isValid();
    }

    @Inject
    AbstractProjectCreateView view;
    @Inject
    RegionControllerAsync regionService;

    @Inject
    Lang lang;
}
