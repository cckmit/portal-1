package ru.protei.portal.ui.official.client.activity.table;

import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.AttachClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;

/**
 * Абстракция активности в таблице должностных лиц
 */
public interface AbstractOfficialTableActivity
        extends ClickColumn.Handler<Official>, EditClickColumn.EditHandler<Official>,
        AttachClickColumn.AttachHandler<Official>, RemoveClickColumn.RemoveHandler<Official>
{

    void onEditClicked(Official value);

    void updateRow(Long officialId);
}
