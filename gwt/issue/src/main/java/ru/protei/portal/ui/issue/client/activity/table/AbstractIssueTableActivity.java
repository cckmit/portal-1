package ru.protei.portal.ui.issue.client.activity.table;

import ru.brainworm.factory.widget.table.client.helper.ClickColumn;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.columns.EditActionClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractIssueTableActivity extends ClickColumn.Handler< CaseObject >, EditActionClickColumn.Handler < CaseObject >, EditActionClickColumn.EditHandler< CaseObject > {

    void onFilterChanged();
    void onEditClicked( CaseObject value );
    void onCreateClick();
}
