package ru.protei.portal.ui.common.client.widget.selector.project;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class ProjectModel extends LifecycleSelectorModel<EntityOption> {

    @Event
    public void onInit(AuthEvents.Success event) {
        clear();
    }

    @Event
    public void onProjectChanged(ProjectEvents.ChangeModel event) {
        refreshOptions();
    }

    @Override
    protected void refreshOptions() {
        regionService.getProjectOptionList(projectQuery, new FluentCallback<List<EntityOption>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(this::notifySubscribers)
        );
    }

    void setPlatformIndependentProject(Boolean platformIndependentProject) {
        this.projectQuery = makeProjectQuery(platformIndependentProject);
        refreshOptions();
    }

    private ProjectQuery makeProjectQuery(Boolean platformIndependentProject) {
        ProjectQuery projectQuery = new ProjectQuery();
        projectQuery.setPlatformIndependentProject(platformIndependentProject);

        return projectQuery;
    }

    @Inject
    RegionControllerAsync regionService;
    @Inject
    Lang lang;

    private ProjectQuery projectQuery = new ProjectQuery();
}
