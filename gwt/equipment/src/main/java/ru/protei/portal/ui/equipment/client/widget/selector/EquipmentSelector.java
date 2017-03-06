package ru.protei.portal.ui.equipment.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.InputSelector;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.equipment.client.common.EquipmentUtils;

import java.util.List;

/**
 * Виджет связанных устройств
 */
public class EquipmentSelector
        extends InputSelector<Equipment >
        implements ModelSelector<Equipment>
    {

        @Inject
        public void init( EquipmentModel model, Lang lang ) {
            model.subscribe( this );
            setSearchEnabled( true );
            setHasNullValue( true );
            setNullOption( lang.equipmentPrimaryUseNotDefinied());
        }

        @Override
        public void fillOptions( List< Equipment > options ) {
            clearOptions();

            for ( Equipment type : options ) {
                addOption( type.getName(), type );
            }
        }

        @Override
        public void refreshValue() {}
}