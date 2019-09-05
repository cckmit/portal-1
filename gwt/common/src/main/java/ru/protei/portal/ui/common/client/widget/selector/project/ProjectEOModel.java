package ru.protei.portal.ui.common.client.widget.selector.project;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ProjectEOModel extends LifecycleSelectorModel<EntityOption> {
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
        regionService.getProjectsList(new FluentCallback<List<ProjectInfo>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(projects -> {
                    contractService.getContracts(new ContractQuery(), new FluentCallback<SearchResult<Contract>>()
                            .withSuccess(sr -> {
                                List<Long> projectIds = sr.getResults()
                                        .stream()
                                        .map(Contract::getProjectId)
                                        .collect(Collectors.toList());

                                List<EntityOption> options = projects
                                        .stream()
                                        .filter(project -> !projectIds.contains(project.getId()))
                                        .map(project -> new EntityOption(project.getName(), project.getId()))
                                        .collect(Collectors.toList());

                                notifySubscribers(options);
                            }));
                }));
    }

    @Inject
    RegionControllerAsync regionService;
    @Inject
    ContractControllerAsync contractService;
    @Inject
    Lang lang;
}