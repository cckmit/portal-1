package ru.protei.portal.ui.common.client.widget.selector.equipment;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

import java.util.Set;
import java.util.stream.Collectors;

public class EquipmentFormSelector extends FormPopupSingleSelector<EquipmentShortView> {

    @Inject
    public void init(EquipmentModel model, Lang lang) {
        this.model = model;
        setAsyncModel(model);
        setSearchAutoFocus(true);
        setItemRenderer( value -> {
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

            return  sb.toString();
        } );
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
//            model.refreshFromCache(this);//TODO
        }
    }

    private EquipmentModel model;
    private boolean hasNullValue = true;
    private boolean printDecimalNumbers = true;
}
