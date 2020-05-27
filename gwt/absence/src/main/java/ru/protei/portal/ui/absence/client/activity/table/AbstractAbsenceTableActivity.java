package ru.protei.portal.ui.absence.client.activity.table;

import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractAbsenceTableActivity extends ClickColumn.Handler<PersonAbsence>, RemoveClickColumn.RemoveHandler<PersonAbsence> {
}
