package ru.protei.portal.app.portal.client.activity.dashboardblocks.table;

import ru.protei.portal.core.model.query.CaseQuery;

/**
 * Модель таблицы дашборда
 */
public class DashboardTableModel {

    public boolean isLoaderShow;
    public Integer daysLimit;
    public CaseQuery query;
    public AbstractDashboardTableView view;

    public DashboardTableModel(AbstractDashboardTableView view, CaseQuery query, boolean isLoaderShow, Integer daysLimit) {
        this(view, query, isLoaderShow);
        this.daysLimit = daysLimit;
    }

    public DashboardTableModel(AbstractDashboardTableView view, CaseQuery query, boolean isLoaderShow) {
        this.view = view;
        this.query = query;
        this.isLoaderShow = isLoaderShow;
    }
}
