package ru.protei.portal.ui.casestate.client.activity.table;

import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

public interface AbstractCaseStateTableActivity
        extends ClickColumn.Handler<CaseState>,
        EditClickColumn.EditHandler<CaseState>
{
}
