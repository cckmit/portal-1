package ru.protei.portal.ui.equipment.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.InputSelector;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.equipment.client.common.EquipmentUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Виджет связанных устройств
 */
public class EquipmentSelector
        extends ButtonSelector<Equipment >
        implements ModelSelector<Equipment>
    {

        @Inject
        public void init( EquipmentModel model, Lang lang ) {
            model.subscribe( this );
            setSearchEnabled( true );
            setHasNullValue( true );
            nullItemOption = new DisplayOption( lang.equipmentPrimaryUseNotDefinied() );
        }

        @Override
        public void fillOptions( List< Equipment > options ) {
            clearOptions();

            for ( Equipment value : options ) {
                StringBuilder sb = new StringBuilder();
                sb.append( value.getName() );
                if ( value.getDecimalNumbers() != null ) {
                    sb
                            .append( " (" )
                            .append( value.getDecimalNumbers().stream().map( EquipmentUtils:: formatNumber ).collect( Collectors.joining(", ")) )
                            .append( ")" );
                }

                addOption( sb.toString(), value );
            }
        }

        @Override
        public void refreshValue() {}
}