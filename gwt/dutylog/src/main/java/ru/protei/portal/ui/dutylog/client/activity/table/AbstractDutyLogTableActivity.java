package ru.protei.portal.ui.dutylog.client.activity.table;

import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

public interface AbstractDutyLogTableActivity extends ClickColumn.Handler<DutyLog>,
        EditClickColumn.EditHandler<DutyLog> {
}
