package ru.protei.portal.ui.employeeregistration.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.ui.common.client.columns.ClickColumn;

public interface AbstractEmployeeRegistrationTableActivity extends ClickColumn.Handler<EmployeeRegistration>,
        InfiniteLoadHandler<EmployeeRegistration> {
}
