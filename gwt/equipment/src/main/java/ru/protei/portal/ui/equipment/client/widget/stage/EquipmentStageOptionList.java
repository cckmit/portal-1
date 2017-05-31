package ru.protei.portal.ui.equipment.client.widget.stage;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.ui.common.client.lang.En_EquipmentStageLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

/**
 * Селектор списка критичности обращения
 */
public class EquipmentStageOptionList extends OptionList<En_EquipmentStage>  {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clearOptions();
        for ( En_EquipmentStage value : En_EquipmentStage.values() ) {
            addOption( lang.getName( value ), value, "form-group col-xs-6 option-" + value.name().toLowerCase() );
        }
    }


    @Inject
    En_EquipmentStageLang lang;
}