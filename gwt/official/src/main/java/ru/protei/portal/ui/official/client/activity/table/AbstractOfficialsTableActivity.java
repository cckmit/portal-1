package ru.protei.portal.ui.official.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.ui.common.client.columns.AttachClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

/**
 * Created by serebryakov on 21/08/17.
 */
public interface AbstractOfficialsTableActivity
        extends InfiniteLoadHandler<Official>, EditClickColumn.EditHandler<Official>, AttachClickColumn.AttachHandler<Official> {

    void onEditClicked(Official value);
}
