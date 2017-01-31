package ru.protei.portal.ui.equipment.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;


/**
 *  Абстрактное представление карточки редактирования единицы оборудования
 */
public interface AbstractEquipmentEditView extends IsWidget {
    void setActivity( AbstractEquipmentEditActivity activity );
}
