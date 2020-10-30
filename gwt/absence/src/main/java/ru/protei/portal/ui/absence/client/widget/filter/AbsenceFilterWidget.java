package ru.protei.portal.ui.absence.client.widget.filter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.AbsenceFilter;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.absence.client.widget.filter.paramview.AbsenceFilterParamWidget;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidget;
import ru.protei.portal.ui.common.client.widget.selector.absence.AbsenceFilterSelector;

public class AbsenceFilterWidget extends FilterWidget<AbsenceFilter, AbsenceQuery, FilterShortView> {

    // Неочивидная последовательность создания виджета super.onInit()
    @Inject
    public AbsenceFilterWidget(AbsenceFilterParamWidget view, AbsenceFilterSelector filter) {
        filterParamView = view;
        filterSelector = filter;
        this.absenceFilterParamWidget = view;
    }

    @Override
    protected AbsenceFilter fillUserFilter() {
        AbsenceFilter filter = new AbsenceFilter();
        filter.setName(filterName.getValue());
        filter.setQuery(filterParamView.getQuery());
        return filter;
    }

    public AbsenceFilterParamWidget getFilterParamView() {
        return absenceFilterParamWidget;
    }

    private final AbsenceFilterParamWidget absenceFilterParamWidget;
}
