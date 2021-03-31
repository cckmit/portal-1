package ru.protei.portal.ui.common.client.widget.project.filter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.ui.common.client.events.ProjectFilterEvents;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidgetModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class ProjectFilterWidgetModel extends FilterWidgetModel<CaseFilterDto<ProjectQuery>> {
    @Override
    protected void saveFilter(CaseFilterDto<ProjectQuery> filter,
                              Consumer<Throwable> onError,
                              Consumer<CaseFilterDto<ProjectQuery>> onSuccess) {

        issueFilterService.saveProjectFilter(filter, new FluentCallback<CaseFilterDto<ProjectQuery>>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new ProjectFilterEvents.Changed()) ))
        );

    }

    @Override
    protected void changeFilter(Long filter, Consumer<Throwable> onError, Consumer<CaseFilterDto<ProjectQuery>> onSuccess) {
        issueFilterService.getCaseFilter(filter, new FluentCallback<CaseFilterDto<ProjectQuery>>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new ProjectFilterEvents.Changed()) ))
        );
    }

    @Override
    protected void removeFilter(Long filter, Consumer<Throwable> onError, Consumer<Long> onSuccess) {
        issueFilterService.removeCaseFilter(filter, new FluentCallback<Long>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new ProjectFilterEvents.Changed()) ))
        );
    }

    @Inject
    CaseFilterControllerAsync issueFilterService;
}
