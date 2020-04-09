package ru.protei.portal.ui.common.client.widget.selector.customertype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.List;

public class CustomerFormSelector extends FormSelector<En_CustomerType> implements SelectorWithModel<En_CustomerType> {

    @Inject
    public void init(CustomerTypeModel model, En_CustomerTypeLang lang) {
        setSelectorModel(model);
        setDisplayOptionCreator(value -> new DisplayOption(value == null ? defaultValue : lang.getName(value)));
    }

    public void fillOptions(List<En_CustomerType> options) {
        clearOptions();

        if(defaultValue != null) {
            addOption(null);
            setValue(null);
        }
        options.forEach(this::addOption);
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}