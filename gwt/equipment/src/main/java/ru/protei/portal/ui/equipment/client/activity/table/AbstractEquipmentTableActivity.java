package ru.protei.portal.ui.equipment.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;

/**
 * Активность таблицы контактов
 */
public interface AbstractEquipmentTableActivity
        extends ClickColumn.Handler< Equipment >, EditClickColumn.EditHandler< Equipment >,
        InfiniteLoadHandler< Equipment >, InfiniteTableWidget.PagerListener
{
    void onEditClicked(Equipment value );
}
