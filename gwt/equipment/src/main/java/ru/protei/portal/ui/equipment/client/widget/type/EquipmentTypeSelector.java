package ru.protei.portal.ui.equipment.client.widget.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.ui.common.client.lang.En_EquipmentTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор типа единицы оборудоваемя
 */
public class EquipmentTypeSelector extends ButtonSelector<En_EquipmentType> {

    @Inject
    public void init( ) {
        fillOptions();
    }
    private void fillOptions() {
        clearOptions();

        for ( En_EquipmentType type : En_EquipmentType.values() ) {
            addOption( typeLang.getName( type ), type );
        }
    }

    @Inject
    En_EquipmentTypeLang typeLang;
}
