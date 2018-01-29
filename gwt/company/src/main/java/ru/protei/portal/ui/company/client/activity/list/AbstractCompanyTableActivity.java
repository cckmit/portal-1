package ru.protei.portal.ui.company.client.activity.list;

import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.protei.portal.ui.common.client.columns.ClickColumn;

/**
 * Created by bondarenko on 17.11.17.
 */
public interface AbstractCompanyTableActivity extends
        ClickColumn.Handler< Company >, EditClickColumn.EditHandler<Company>,
        InfiniteLoadHandler<Company>, InfiniteTableWidget.PagerListener {
}