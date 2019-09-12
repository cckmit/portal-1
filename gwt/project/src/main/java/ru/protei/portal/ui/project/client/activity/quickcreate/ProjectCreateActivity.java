package ru.protei.portal.ui.project.client.activity.quickcreate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

/**
 * Активность создания проекта с минимальным набором параметров
 */
public abstract class ProjectCreateActivity implements AbstractProjectCreateActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ProjectEvents.QuickCreate event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        initialView(new ProjectInfo());
    }

    @Event
    public void onProductListChanged(ProductEvents.ProductListChanged event) {
        view.refreshProducts();
    }

    @Event
    public void onSetProduct(ProductEvents.Set event) {
        if (event.product == null) return;
        view.products().getValue().add(event.product.toProductShortView());
    }

    @Override
    public void onSaveClicked() {

        if (!validate()) {
            return;
        }

        fillProject();
        regionService.saveProject(project, new FluentCallback<ProjectInfo>()
                .withErrorMessage(lang.errNotSaved())
                .withSuccess(projectInfo -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ProjectEvents.Set(projectInfo));
                    initialView(new ProjectInfo());
                }));
    }

    @Override
    public void onResetClicked() {
        initialView(new ProjectInfo());
    }

    private void initialView(ProjectInfo project) {
        this.project = project;
        view.name().setValue(project.getName());
        view.description().setValue(project.getDescription());
        view.region().setValue(project.getRegion());
        view.direction().setValue(project.getProductDirection() == null ? null : new ProductDirectionInfo(project.getProductDirection()));
        view.customerType().setValue(project.getCustomerType());
        view.company().setValue(EntityOption.fromCompany(project.getCustomer()));
        view.products().setValue(project.getProducts());
    }

    private void fillProject() {
        project.setName(view.name().getValue().trim());
        project.setDescription(view.description().getValue().trim());
        project.setRegion(view.region().getValue());
        project.setProductDirection(EntityOption.fromProductDirectionInfo(view.direction().getValue()));
        project.setCustomerType(view.customerType().getValue());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setProducts(view.products().getValue());
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

    private ProjectInfo project;
}