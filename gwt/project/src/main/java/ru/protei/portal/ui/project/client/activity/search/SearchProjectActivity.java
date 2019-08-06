package ru.protei.portal.ui.project.client.activity.search;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProductEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Активность поиска проекта
 */
public abstract class SearchProjectActivity implements Activity, AbstractSearchProjectActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ProjectEvents.Search event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
    }

    @Event
    public void onProductListChanged(ProductEvents.ProductListChanged event) {
        view.loadProducts();
    }

    @Event
    public void onProjectListChanged(ProjectEvents.ChangeModel event) {
        requestProjects();
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
    public void onProjectChanged() {
        ProjectInfo project = view.project().getValue();
        if (project != null) {
            fireEvent(new ProjectEvents.Set(project));
        }
    }

    private void requestProjects() {
        ProjectQuery query = makeQuery();
        if (isQueryNotValid(query)) {
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
        } else {
            view.clearProjectList();
            regionService.getProjectsList(query, new FluentCallback<List<ProjectInfo>>()
                    .withErrorMessage(lang.errGetList())
                    .withSuccess(result -> {
                        view.fillProjectList(result);
                    }));
        }
    }

    private ProjectQuery makeQuery() {
        ProjectQuery query = new ProjectQuery(view.name().getValue(), En_SortField.creation_date, En_SortDir.DESC);
        DateInterval createdInterval = view.dateCreatedRange().getValue();
        if (createdInterval != null) {
            query.setCreatedFrom(createdInterval.from);
            query.setCreatedTo(createdInterval.to);
        }
        query.setCustomerType(view.customerType().getValue());
        query.setProductIds(view.products().getValue().stream().map(product -> product.getId()).collect(Collectors.toList()));
        return query;
    }

    private boolean isQueryNotValid(ProjectQuery query) {
        return query == null || !query.isParamsPresent();
    }

    @Inject
    RegionControllerAsync regionService;

    @Inject
    Lang lang;

    @Inject
    AbstractSearchProjectView view;
}
