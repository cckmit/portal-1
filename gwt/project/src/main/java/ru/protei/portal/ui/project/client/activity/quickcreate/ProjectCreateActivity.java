package ru.protei.portal.ui.project.client.activity.quickcreate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.HomeCompanyService;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.person.AsyncPersonModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.util.CrmConstants.State.UNKNOWN;

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

        view.products().setValue(event.product.toProductShortView() == null? null : setOf(event.product.toProductShortView()));
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
                    fireEvent(new DocumentEvents.ProjectCreated());
                }));
    }

    @Override
    public void onResetClicked() {
        initialView(new Project());
    }

    @Override
    public void onDirectionChanged() {
        final Set<ProductDirectionInfo> directions = view.directions().getValue();

        if (isEmpty(directions)) {
            view.productEnabled().setEnabled(false);
            view.updateProductModel(new HashSet<>());
            view.products().setValue(null);
        } else {
            view.productEnabled().setEnabled(true);
            view.updateProductModel(toSet(directions, ProductDirectionInfo::getId));
            view.products().setValue(
                    stream(view.products().getValue()).
                            filter(productShortView -> {
                                final Set<Long> ids = toSet(productShortView.getProductDirection(), ProductDirectionInfo::getId);
                                return stream(directions).anyMatch(direction ->
                                        ids.contains(direction.id));
                            })
                            .collect(Collectors.toSet())
            );
        }
    }

    @Override
    public void onProductChanged() {
        final Set<ProductShortView> currentComplexes = stream(view.products().getValue())
                .filter(info -> info.getType() == En_DevUnitType.COMPLEX && info.getProductDirection() != null)
                .collect(Collectors.toSet());
        Set<ProductShortView> addedComplex = new HashSet<>(currentComplexes);
        addedComplex.removeAll(selectedComplexes);
        if (isNotEmpty(addedComplex)) {
            final Set<ProductDirectionInfo> directions = view.directions().getValue();
            directions.addAll(stream(currentComplexes)
                    .flatMap(productShortView -> stream(productShortView.getProductDirection()))
                    .collect(Collectors.toSet()));
            view.directions().setValue(directions);
            onDirectionChanged();
        }
        selectedComplexes = currentComplexes;
    }

    private void initialView(Project project) {
        this.project = project;
        view.name().setValue(project.getName());
        view.description().setValue(project.getDescription());
        view.region().setValue(project.getRegion());
        view.directions().setValue(toSet(project.getProductDirectionEntityOptionList(), option -> new ProductDirectionInfo(option)));
        view.customerType().setValue(project.getCustomerType());
        view.productEnabled().setEnabled(false);
        view.company().setValue(EntityOption.fromCompany(project.getCustomer()));

        view.products().setValue(new HashSet<>(emptyIfNull(project.getProductShortViewList())));
        selectedComplexes = new HashSet<>();

        view.updateProductModel( toSet(project.getProductDirectionEntityOptionList(), EntityOption::getId));        selectedComplexes.addAll(stream(project.getProductShortViewList()).filter(product -> product.getType() == En_DevUnitType.COMPLEX).collect(Collectors.toSet()) );

        view.headManagers().setValue(new HashSet<>(emptyIfNull(project.getTeam())));
        homeCompanyService.getAllHomeCompanies(homeCompanies -> {
            asyncPersonModel.updateCompanies( CollectionUtils.toSet(homeCompanies, EntityOption::getId) );
            if (isEmpty( homeCompanies )) {
                view.headManagers().setValue( null );
            }
        });
    }

    private void fillProject() {
        project.setName(view.name().getValue().trim());
        project.setDescription(view.description().getValue().trim());
        project.setRegion(view.region().getValue());
        project.setProductDirections( toSet(view.directions().getValue(), DevUnit::fromProductDirectionInfo));
        project.setCustomerType(view.customerType().getValue());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setProducts( toSet(view.products().getValue(), DevUnit::fromProductShortView));
        project.setTeam(toPersonProjectMemberViewList(view.headManagers().getValue()));
        project.setStateId(UNKNOWN);
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
                        new PersonProjectMemberView(personShortView.getName(), personShortView.getId(), personShortView.isFired(), En_PersonRoleType.HEAD_MANAGER))
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
    private Set<ProductShortView> selectedComplexes;
}
