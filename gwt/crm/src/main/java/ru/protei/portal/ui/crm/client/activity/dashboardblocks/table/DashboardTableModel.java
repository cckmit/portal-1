package ru.protei.portal.ui.crm.client.activity.dashboardblocks.table;

import ru.protei.portal.core.model.query.CaseQuery;

/**
 * Модель таблицы дашборда
 */
public class DashboardTableModel {

    public boolean isLoaderShow;
    public CaseQuery query;
    public AbstractDashboardTableView view;

    public DashboardTableModel(AbstractDashboardTableView view, CaseQuery query, boolean isLoaderShow) {
        this.view = view;
        this.query = query;
        this.isLoaderShow = isLoaderShow;
    }
}