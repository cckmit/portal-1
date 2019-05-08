package ru.protei.portal.ui.common.client.widget.selector.equipment;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Виджет связанных устройств
 */
public class EquipmentButtonSelector
        extends ButtonSelector<EquipmentShortView >
        implements SelectorWithModel<EquipmentShortView> {

    @Inject
    public void init(Lang lang) {
        setSearchEnabled( true );
        setSearchAutoFocus(true);
        setDisplayOptionCreator( value -> {
            StringBuilder sb = new StringBuilder();
            if ( value == null ) {
                sb.append( lang.equipmentPrimaryUseNotDefinied() );
            } else {
                sb.append( value.getName() == null ? "" : value.getName() );
                if ( printDecimalNumbers && value.getDecimalNumbers() != null ) {
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

    @Override
    public void refreshValue() {}

    public void setModel(EquipmentModel model) {
        this.model = model;
        setSelectorModel(model);
    }

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

    public void setVisibleTypes(Set<En_EquipmentType> types) {
        if (model != null) {
            model.setVisibleTypes(types);
        }
    }

    public void setProjectId(Long projectId) {
        if (model != null) {
            model.setProjectId(projectId);
        }
    }

    public void setPrintDecimalNumbers(boolean isPrintDecimalNumbers) {
        this.printDecimalNumbers = isPrintDecimalNumbers;
        if (model != null) {
            model.refreshFromCache(this);
        }
    }

    private EquipmentModel model;
    private boolean hasNullValue = true;
    private boolean printDecimalNumbers = true;
}