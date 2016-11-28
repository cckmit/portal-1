package ru.protei.portal.ui.common.client.widget.selector.contacttype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор для типов ContactItem {@link ru.protei.portal.core.model.struct.ContactItem}
 */
public class ContactTypeButtonSelector extends ButtonSelector<En_ContactItemType>{

    public void fillOptions(List<En_ContactItemType> items){
        clearOptions();

        if(defaultValue != null) {
            addOption(defaultValue, null);
            setValue(null);
        }

        items.forEach(option -> addOption(option.getMessage(lang), option));
    }

    public void setDefaultValue( String value ) {
        addOption( value , null );
    }

    private String defaultValue = null;

    @Inject
    Lang lang;
}
