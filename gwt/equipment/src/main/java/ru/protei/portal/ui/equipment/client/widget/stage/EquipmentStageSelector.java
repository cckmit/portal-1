package ru.protei.portal.ui.equipment.client.widget.stage;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.ui.common.client.lang.En_EquipmentStageLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор стадии разработки единицы оборудоваемя
 */
public class EquipmentStageSelector extends ButtonSelector<En_EquipmentStage> {

    @Inject
    public void init( En_EquipmentStageLang stageLang ) {
        setHasNullValue( false );
        setDisplayOptionCreator( value -> new DisplayOption( stageLang.getName( value )) );
        fillOptions();
    }

    private void fillOptions() {
        clearOptions();

        for ( En_EquipmentStage stage : En_EquipmentStage.values() ) {
            addOption( stage );
        }
    }
}
