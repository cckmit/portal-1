package ru.protei.portal.ui.common.client.view.projectfilter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.ui.common.client.events.ProjectFilterEvents;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidgetModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class ProjectFilterWidgetModel extends FilterWidgetModel<CaseFilterDto<ProjectQuery>> {
    @Override
    protected void saveFilter(CaseFilterDto<ProjectQuery> filter,
                              Consumer<Throwable> onError,
                              Consumer<CaseFilterDto<ProjectQuery>> onSuccess) {

        issueFilterService.saveIssueFilter(filter, new FluentCallback<CaseFilterDto<ProjectQuery>>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new ProjectFilterEvents.Changed()) ))
        );

    }

    @Override
    protected void changeFilter(Long filter, Consumer<Throwable> onError, Consumer<CaseFilterDto<ProjectQuery>> onSuccess) {
        issueFilterService.getIssueFilter(filter, new FluentCallback<CaseFilterDto<ProjectQuery>>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new ProjectFilterEvents.Changed()) ))
        );
    }

    @Override
    protected void removeFilter(Long filter, Consumer<Throwable> onError, Consumer<Long> onSuccess) {
        issueFilterService.removeIssueFilter(filter, new FluentCallback<Long>()
                .withError(onError)
                .withSuccess(onSuccess.andThen( caseFilterDto -> fireEvent(new ProjectFilterEvents.Changed()) ))
        );
    }

    @Inject
    IssueFilterControllerAsync issueFilterService;
}
