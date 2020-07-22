package ru.protei.portal.ui.absence.client.widget;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.AbsenceFilterShortView;
import ru.protei.portal.ui.absence.client.view.report.paramview.AbsenceFilterParamView;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidget;
import ru.protei.portal.ui.common.client.widget.selector.absence.AbsenceFilterSelector;

public class AbsenceFilterWidget extends FilterWidget<AbsenceFilter, AbsenceQuery, AbsenceFilterShortView> {
    @Inject
    public AbsenceFilterWidget(AbsenceFilterParamView view, AbsenceFilterSelector filter) {
        filterParamView = view;
        filterSelector = filter;
        this.absenceFilterParamView = view;
    }

    @Override
    protected AbsenceFilter fillUserFilter() {
        AbsenceFilter filter = new AbsenceFilter();
        filter.setName(filterName.getValue());
        AbsenceQuery query = filterParamView.getQuery();
        filter.setQuery(query);
        return filter;
    }

    public AbsenceFilterParamView getFilterParamView() {
        return absenceFilterParamView;
    }

    private final AbsenceFilterParamView absenceFilterParamView;
}
