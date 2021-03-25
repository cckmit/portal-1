package ru.protei.portal.ui.project.client.widget.filter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.filterwidget.AbstractFilterShortView;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidget;
import ru.protei.portal.ui.common.client.widget.selector.project.filter.ProjectFilterSelector;
import ru.protei.portal.ui.project.client.widget.filter.paramview.ProjectFilterParamWidget;

public class ProjectFilterWidget extends FilterWidget<CaseFilterDto<ProjectQuery>, ProjectQuery, AbstractFilterShortView> {
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

    private final ProjectFilterParamWidget projectFilterParamWidget;
}
