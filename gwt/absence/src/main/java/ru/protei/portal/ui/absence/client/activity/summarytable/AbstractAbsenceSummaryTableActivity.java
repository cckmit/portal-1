package ru.protei.portal.ui.absence.client.activity.summarytable;

import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractAbsenceSummaryTableActivity extends ClickColumn.Handler<PersonAbsence>,
        EditClickColumn.EditHandler<PersonAbsence>,
        RemoveClickColumn.RemoveHandler<PersonAbsence> {

    void onCompleteAbsence(PersonAbsence absence);
}