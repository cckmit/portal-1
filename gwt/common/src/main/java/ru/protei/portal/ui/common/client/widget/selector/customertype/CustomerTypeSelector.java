package ru.protei.portal.ui.common.client.widget.selector.customertype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class CustomerTypeSelector extends ButtonSelector<En_CustomerType> {

    @Inject
    public void init(En_CustomerTypeLang lang) {
        setDisplayOptionCreator(value -> new DisplayOption(value == null ? defaultValue : lang.getName(value)));
    }

    public void fillOptions() {
        clearOptions();

        if(defaultValue != null) {
            addOption(null);
            setValue(null);
        }

        for (En_CustomerType type : En_CustomerType.values()) {
            addOption(type);
        }
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
        fillOptions();
    }

    private String defaultValue = null;
}
