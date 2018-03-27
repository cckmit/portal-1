package ru.protei.portal.ui.equipment.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Виджет связанных устройств
 */
public class EquipmentSelector
        extends ButtonSelector<EquipmentShortView >
        implements ModelSelector<EquipmentShortView>
    {

        @Inject
        public void init( EquipmentModel model, Lang lang ) {
            model.subscribe( this );
            setSearchEnabled( true );
            setHasNullValue( true );
            setSearchAutoFocus(true);

            setDisplayOptionCreator( value -> {
                StringBuilder sb = new StringBuilder();
                if ( value == null ) {
                    sb.append( lang.equipmentPrimaryUseNotDefinied() );
                } else {
                    sb.append( value.getName() == null ? "" : value.getName() );
                    if ( value.getDecimalNumbers() != null ) {
                        sb
                                .append( " (" )
                                .append( value.getDecimalNumbers().stream().map( DecimalNumberFormatter:: formatNumber ).collect( Collectors.joining( ", " ) ) )
                                .append( ")" );
                    }
                }

                return new DisplayOption( sb.toString() );
            } );
        }

        @Override
        public void fillOptions( List< EquipmentShortView > options ) {
            clearOptions();
            if (hasNullValue) {
                addOption(null);
            }
            
            options.forEach( this::addOption );
        }

        public void setHasNullValue(boolean hasNullValue) {
            this.hasNullValue = hasNullValue;
        }

        @Override
        public void refreshValue() {}

        private boolean hasNullValue;
    }