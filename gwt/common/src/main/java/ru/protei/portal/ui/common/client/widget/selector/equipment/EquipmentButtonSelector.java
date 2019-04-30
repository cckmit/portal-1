package ru.protei.portal.ui.common.client.widget.selector.equipment;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Виджет связанных устройств
 */
public class EquipmentButtonSelector
        extends ButtonSelector<EquipmentShortView >
        implements SelectorWithModel<EquipmentShortView> {

    @Inject
    public void init( EquipmentModel model, Lang lang ) {
        this.model = model;
        setSelectorModel(model);
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
        model.subscribe(this, projectId, types);
    }

    @Override
    public void fillOptions( List< EquipmentShortView > options ) {
        this.options = options;

        clearOptions();
        if (hasNullValue) {
            addOption(null);
        }

        options.forEach( this::addOption );
    }

    @Override
    public void refreshValue() {}

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

    public void setVisibleTypes(Set<En_EquipmentType> types) {
        this.types = types;
        if (model != null) {
            model.subscribe(this, projectId, types);
        }
    }

    public void setPrintDecimalNumbers(boolean isPrintDecimalNumbers) {
        this.printDecimalNumbers = isPrintDecimalNumbers;
        refillOptions();
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
        if (model != null) {
            model.subscribe(this, projectId, types);
        }
    }

    private void refillOptions() {
        fillOptions(options);
    }

    private List<EquipmentShortView> options = Collections.emptyList();
    private boolean hasNullValue = true;
    private boolean printDecimalNumbers = true;
    private EquipmentModel model;
    private Long projectId = null;
    private Set<En_EquipmentType> types = null;
}