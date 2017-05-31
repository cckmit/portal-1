package ru.protei.portal.ui.equipment.client.activity.copy;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстрактный вид диалога копирования оборудования
 */
public interface AbstractEquipmentCopyView extends IsWidget {
    void setActivity( AbstractEquipmentCopyActivity activity );

    HasValue<String> name();
}
