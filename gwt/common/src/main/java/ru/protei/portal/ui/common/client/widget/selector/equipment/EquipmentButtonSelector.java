package ru.protei.portal.ui.common.client.widget.selector.equipment;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.buttonselector.ButtonPopupSingleSelector;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Виджет связанных устройств
 */
public class EquipmentButtonSelector extends ButtonPopupSingleSelector<EquipmentShortView> {

    @Inject
    public void init(EquipmentModel model, Lang lang) {
        this.model = model;
        setAsyncSelectorModel(model);
        setSelectorItemRenderer( value -> {
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

            return sb.toString();
        } );
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
    }

    private EquipmentModel model;
    private boolean printDecimalNumbers = true;
}