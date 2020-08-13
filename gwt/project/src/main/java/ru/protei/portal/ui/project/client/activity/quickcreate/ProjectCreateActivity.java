package ru.protei.portal.ui.project.client.activity.quickcreate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.HomeCompanyService;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.person.AsyncPersonModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

/**
 * Активность создания проекта с минимальным набором параметров
 */
public abstract class ProjectCreateActivity implements AbstractProjectCreateActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setManagersModel(asyncPersonModel);
    }

    @Event
    public void onShow(ProjectEvents.QuickCreate event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        initialView(new Project());
    }

    @Event
    public void onSetProduct(ProductEvents.Set event) {
        if (event.product == null) {
            return;
        }

        view.product().setValue(event.product.toProductShortView());
    }

    @Override
    public void onSaveClicked() {

        if (!validate()) {
            return;
        }

        fillProject();
        regionService.saveProject(project, new FluentCallback<UiResult<Project>>()
                .withErrorMessage(lang.errNotSaved())
                .withSuccess(project -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ProjectEvents.Set(new EntityOption(project.getData().getName(), project.getData().getId())));
                    initialView(new Project());
                }));
    }

    @Override
    public void onResetClicked() {
        initialView(new Project());
    }

    @Override
    public void onDirectionChanged() {
        view.updateProductDirection(view.direction().getValue() == null ? null : view.direction().getValue().id);
        view.product().setValue(null);
    }

    private void initialView(Project project) {
        this.project = project;
        view.name().setValue(project.getName());
        view.description().setValue(project.getDescription());
        view.region().setValue(project.getRegion());
        view.direction().setValue(project.getProductDirection() == null ? null : new ProductDirectionInfo(project.getProductDirection()));
        view.customerType().setValue(project.getCustomerType());
        view.company().setValue(EntityOption.fromCompany(project.getCustomer()));
        view.product().setValue(project.getSingleProduct());
        view.updateProductDirection(project.getProductDirection() == null ? null : project.getProductDirection().getId());
        view.headManagers().setValue(new HashSet<>(emptyIfNull(project.getTeam())));
        homeCompanyService.getAllHomeCompanies(homeCompanies -> view.setCompaniesSupplier(() -> new HashSet<>(homeCompanies)));
    }

    private void fillProject() {
        project.setName(view.name().getValue().trim());
        project.setDescription(view.description().getValue().trim());
        project.setRegion(view.region().getValue());
        project.setProductDirection(EntityOption.fromProductDirectionInfo(view.direction().getValue()));
        project.setCustomerType(view.customerType().getValue());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setProducts(new HashSet<>(view.product().getValue() == null ? Collections.emptyList() : Collections.singleton(view.product().getValue())));
        project.setTeam(toPersonProjectMemberViewList(view.headManagers().getValue()));
    }

    private boolean validate() {
        return view.nameValidator().isValid() &&
                view.directionValidator().isValid() &&
                view.customerTypeValidator().isValid() &&
                view.companyValidator().isValid() &&
                view.headManagersValidator().isValid();
    }

    private List<PersonProjectMemberView> toPersonProjectMemberViewList(Collection<PersonShortView> persons) {
        return CollectionUtils.emptyIfNull(persons)
                .stream()
                .map(personShortView ->
                        new PersonProjectMemberView(personShortView.getName(), personShortView.getId(), personShortView.isFired(), En_DevUnitPersonRoleType.HEAD_MANAGER))
                .collect(Collectors.toList());
    }

    @Inject
    AbstractProjectCreateView view;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    AsyncPersonModel asyncPersonModel;
    @Inject
    HomeCompanyService homeCompanyService;

    @Inject
    Lang lang;

    private Project project;
}
