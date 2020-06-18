package ru.protei.portal.ui.common.client.widget.selector.rangetype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.ui.common.client.lang.En_DateIntervalLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор для типов временных интервалов
 */
public class RangeTypeButtonSelector extends ButtonSelector<En_DateIntervalType> {

    @Inject
    public void init( ) {
        setDisplayOptionCreator( value -> new DisplayOption( lang.getName( value )) );
    }

    public void fillOptions(List<En_DateIntervalType> items){
        clearOptions();
        items.forEach(this::addOption);
    }

    @Inject
    En_DateIntervalLang lang;
}
