package ru.protei.portal.ui.dutylog.client.widget.filter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.DutyLogFilter;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterWidget;
import ru.protei.portal.ui.common.client.widget.selector.dutylog.DutyLogFilterSelector;
import ru.protei.portal.ui.dutylog.client.widget.filter.paramview.DutyLogFilterParamWidget;

public class DutyLogFilterWidget extends FilterWidget<DutyLogFilter, DutyLogQuery, FilterShortView> {

    // Неочивидная последовательность создания виджета super.onInit()
    @Inject
    public DutyLogFilterWidget(DutyLogFilterParamWidget view, DutyLogFilterSelector filter) {
        filterParamView = view;
        filterSelector = filter;
        this.dutyLogFilterParamWidget = view;
    }

    @Override
    protected DutyLogFilter fillUserFilter() {
        DutyLogFilter filter = new DutyLogFilter();
        filter.setName(filterName.getValue());
        filter.setQuery(filterParamView.getQuery());
        return filter;
    }

    public DutyLogFilterParamWidget getFilterParamView() {
        return dutyLogFilterParamWidget;
    }

    private final DutyLogFilterParamWidget dutyLogFilterParamWidget;
}

