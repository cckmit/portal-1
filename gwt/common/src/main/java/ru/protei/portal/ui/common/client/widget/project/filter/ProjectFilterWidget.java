package ru.protei.portal.ui.common.client.widget.project.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidget;
import ru.protei.portal.ui.common.client.widget.project.filter.paramview.ProjectFilterParamWidget;
import ru.protei.portal.ui.common.client.widget.selector.project.filter.ProjectFilterSelector;

public class ProjectFilterWidget extends FilterWidget<CaseFilterDto<ProjectQuery>, ProjectQuery, FilterShortView> {
    @Inject
    public ProjectFilterWidget(ProjectFilterParamWidget projectFilterParamWidget,
                               ProjectFilterSelector projectFilterSelector) {
        filterParamView = projectFilterParamWidget;
        filterSelector = projectFilterSelector;
        this.projectFilterParamWidget = projectFilterParamWidget;
    }

    @Override
    public ProjectFilterParamWidget getFilterParamView() {
        return projectFilterParamWidget;
    }

    @Override
    protected CaseFilterDto<ProjectQuery> fillUserFilter() {
        CaseFilterDto<ProjectQuery> caseFilterDto = new CaseFilterDto<>();
        CaseFilter caseFilter = new CaseFilter();
        caseFilter.setType(En_CaseFilterType.PROJECT);
        caseFilter.setName(filterName.getValue());

        caseFilterDto.setQuery(filterParamView.getQuery());
        caseFilterDto.setCaseFilter(caseFilter);

        return caseFilterDto;
    }

    public HasValue<FilterShortView> userFilter() {
        return filterSelector;
    }

    private final ProjectFilterParamWidget projectFilterParamWidget;
}
