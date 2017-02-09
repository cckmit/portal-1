package ru.protei.portal.ui.equipment.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.InputSelector;
import ru.protei.portal.ui.common.shared.model.OrganizationCode;
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
                addOption( buildEquipmentName( type ), type );
            }
        }

        @Override
        public void refreshValue() {}

        private String buildEquipmentName( Equipment value ) {
            StringBuilder sb = new StringBuilder();
            sb.append( value.getName() );


            if ( value.getPAMR_RegisterNumber() != null ) {
                sb.append( " / " + EquipmentUtils.formatNumberByStringValues( OrganizationCode.PAMR,
                        value.getClassifierCode(), value.getPAMR_RegisterNumber() ) );
            }

            if ( value.getPDRA_RegisterNumber() != null ) {
                sb.append( " / " + EquipmentUtils.formatNumberByStringValues( OrganizationCode.PDRA,
                        value.getClassifierCode(), value.getPDRA_RegisterNumber() ) );
            }

            return sb.toString();
        }


}