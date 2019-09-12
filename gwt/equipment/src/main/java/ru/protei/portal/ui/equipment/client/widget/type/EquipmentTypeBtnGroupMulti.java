package ru.protei.portal.ui.equipment.client.widget.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.ui.common.client.lang.En_EquipmentTypeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;


/**
 * Типы единиц оборудования
 */
public class EquipmentTypeBtnGroupMulti extends ToggleBtnGroupMulti< En_EquipmentType > {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();

        for ( En_EquipmentType type : En_EquipmentType.values() ) {
            addBtnWithImage( "./images/eq_" + type.name().toLowerCase() + ".png", "btn btn-default no-border eq-type", null, type, typeLang.getName( type ) );
        }
    }

    @Inject
    En_EquipmentTypeLang typeLang;
}
