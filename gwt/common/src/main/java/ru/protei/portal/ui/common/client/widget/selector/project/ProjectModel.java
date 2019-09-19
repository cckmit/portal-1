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
        if (!isLazy || !lock) {
            regionService.getProjectsEntityOptionList(projectQuery, new FluentCallback<List<EntityOption>>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(this::notifySubscribers)
            );
        }
    }

    void setIndependentProject(Boolean independentProject) {
        this.projectQuery = makeQuery(independentProject);
        lock = false;
        refreshOptions();
        lock = true;
    }

    void setLazy(boolean isLazy) {
        this.isLazy = isLazy;
    }

    private ProjectQuery makeQuery(Boolean independentProject) {
        ProjectQuery projectQuery = new ProjectQuery();
        projectQuery.setIndependentProject(independentProject);

        return projectQuery;
    }

    @Inject
    RegionControllerAsync regionService;
    @Inject
    Lang lang;

    private boolean lock = true;
    private boolean isLazy;
    private ProjectQuery projectQuery = new ProjectQuery();
}
