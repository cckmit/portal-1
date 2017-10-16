package ru.protei.portal.ui.common.client.widget.selector.contacttype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Селектор для типов ContactItem {@link ru.protei.portal.core.model.struct.ContactItem}
 */
public class ContactTypeButtonSelector extends ButtonSelector<En_ContactItemType> {

    @Inject
    public void init( Lang lang ) {
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getMessage( lang ) ) );
    }

    public void fillOptions( List<En_ContactItemType> items){
        clearOptions();

        if( defaultValue != null ) {
            addOption(null);
            setValue(null);
        }

        items.forEach(this::addOption);
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}
