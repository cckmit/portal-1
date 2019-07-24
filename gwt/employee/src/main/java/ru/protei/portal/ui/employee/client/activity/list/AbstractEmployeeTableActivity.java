package ru.protei.portal.ui.employee.client.activity.list;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

public interface AbstractEmployeeTableActivity extends
        ClickColumn.Handler<EmployeeShortView>, EditClickColumn.EditHandler<EmployeeShortView>,
        InfiniteLoadHandler<EmployeeShortView>, InfiniteTableWidget.PagerListener {
}
