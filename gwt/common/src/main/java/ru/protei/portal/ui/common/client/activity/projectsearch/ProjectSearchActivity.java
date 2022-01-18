package ru.protei.portal.ui.common.client.activity.projectsearch;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.toSet;

/**
 * Активность поиска проекта
 */
public abstract class ProjectSearchActivity implements Activity, AbstractProjectSearchActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ProjectEvents.Search event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        view.resetFilter();
        view.clearProjectList();
        view.setVisibleProducts(event.showProducts);
        view.setVisibleManagers(event.showManagers);
        view.setSeparateFormView(event.separateFormView);
    }

    @Override
    public void onSearchClicked() {
        requestProjects();
    }

    @Override
    public void onResetClicked() {
        view.resetFilter();
    }

    @Override
    public void onProjectSelected() {
        ProjectInfo projectInfo = view.project().getValue();
        if (projectInfo != null) {
            fireEvent(new ProjectEvents.Set(new EntityOption(projectInfo.getName(), projectInfo.getId())));
        }
    }

    private void requestProjects() {
        ProjectQuery query = makeQuery();
        view.clearProjectList();
        regionService.getProjectInfoList(query, new FluentCallback<List<ProjectInfo>>()
                .withErrorMessage(lang.errGetList())
                .withSuccess(result -> {
                    view.fillProjectList(result);
                }));
    }

    private ProjectQuery makeQuery() {
        ProjectQuery query = new ProjectQuery(view.name().getValue(), view.id().getValue(), En_SortField.project_creation_date, En_SortDir.DESC);
        DateInterval createdInterval = view.dateCreatedRange().getValue();
        if (createdInterval != null) {
            query.setCreatedFrom(createdInterval.from);
            query.setCreatedTo(createdInterval.to);
        }
        query.setCustomerType(view.customerType().getValue());
        query.setProductIds(view.products().getValue().stream().map(product -> product.getId()).collect(Collectors.toSet()));
        query.setHeadManagerIds(toSet(view.managers().getValue(), PersonShortView::getId));
        query.setLimit(100);
        return query;
    }

    @Inject
    RegionControllerAsync regionService;

    @Inject
    Lang lang;

    @Inject
    AbstractProjectSearchView view;
}
