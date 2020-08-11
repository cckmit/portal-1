package ru.protei.portal.ui.absence.client.activity.summarytable;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

public interface AbstractAbsenceSummaryTableActivity extends ClickColumn.Handler<PersonAbsence>,
        EditClickColumn.EditHandler<PersonAbsence>,
        RemoveClickColumn.RemoveHandler<PersonAbsence>,
        InfiniteLoadHandler<PersonAbsence>, InfiniteTableWidget.PagerListener {

    void onCompleteAbsence(PersonAbsence absence);
    void onFilterChange();
}