package ru.protei.portal.ui.common.client.widget.homecompany;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeCompanyMultiSelector extends MultipleInputSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    public void init(HomeCompanyModel model, Lang lang) {
        model.subscribe(this);
        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
    }

    @Override
    public void fillOptions(List<EntityOption> options) {
        clearOptions();
        if (reverseOrder) {
            List<EntityOption> reversed = new ArrayList<>(options);
            Collections.reverse(reversed);
            reversed.forEach(option -> addOption(option.getDisplayText(), option));
        } else {
            options.forEach(option -> addOption(option.getDisplayText(), option));
        }
    }

    public void setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
    }

    private boolean reverseOrder = false;
}
