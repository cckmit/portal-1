package ru.protei.portal.ui.project.client.activity.table.detailed;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class ProjectDetailedTableActivity implements AbstractProjectDetailedTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow(ProjectEvents.ShowDetailedTable event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        requestProjects(event.query);
    }

    @Override
    public void onItemClicked(ProjectInfo value) {

    }

    private void requestProjects(ProjectQuery query) {

        view.clearRecords();

        regionService.getProjectsList(query, new FluentCallback<List<ProjectInfo>>()
                .withErrorMessage(lang.errGetList())
                .withSuccess(result -> view.addRecords(result))
        );
    }

    @Inject
    AbstractProjectDetailedTableView view;

    @Inject
    RegionControllerAsync regionService;

    @Inject
    Lang lang;
}
